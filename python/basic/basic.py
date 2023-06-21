from dataclasses import dataclass
import string
from copy import copy

TN_LEFT_BRACKET = 'LEFT_BRACKET'
TN_RIGHT_BRACKET = 'RIGHT_BRACKET'
TN_SQ_LEFT_BRACKET = 'TN_SQ_LEFT_BRACKET'
TN_SQ_RIGHT_BRACKET = 'TN_SQ_RIGHT_BRACKET'
TN_INT = 'INT'
TN_FLOAT = 'FLOAT'
TN_STRING = 'STRING'
TN_PLUS = 'PLUS'
TN_MINUS = 'MINUS'
TN_MULTIPLY = 'MULTIPLY'
TN_DIVIDE = 'DIVIDE'
TN_EQ = 'EQ'
TN_NEQ = 'NEQ'
TN_EE = 'EE'
TN_GR = 'GR'
TN_GRE = 'GRE'
TN_LE = 'LE'
TN_LEE = 'LEE'
TN_ARROW = 'ARROW'
TN_COMMA = 'COMMA'
TN_IDENTEFIER = 'IDENTEFIER'
TN_KEYWORD = 'KEYWORD'
TN_EOF = 'END_OF_FILE'
DIGESTS = '0123456789'

KEYWORDS = [
    "VAR",
    "NOT",
    "AND",
    "OR",
    "IF",
    "THEN",
    "ELIF",
    "ELSE",
    "FOR",
    "TO",
    "STEP",
    "WHILE",
    "FUNC",
]

VERIABLE_LETTERS = string.ascii_letters + string.digits + "_"

OPERATION_MAP = {
    TN_MINUS: '-',
    TN_PLUS: '+',
    TN_MULTIPLY: '*',
    TN_DIVIDE: '/',
}

BOOL_OPERATION_MAP = {
    TN_EE: '==',
    TN_NEQ: '!=',
    TN_GR: '>',
    TN_GRE: '>=',
    TN_LE: '<',
    TN_LEE: '<=',
}


class LexerException(Exception):
    pass


class LexerSyntaxError(LexerException):
    pass


class UnExpectedCharacterError(LexerException):
    pass

class RuntimeException(Exception):
    pass


class IllegalOperationException(RuntimeException):
    pass


class ParserSyntaxError(Exception):
    def __init__(self, msg, col, ln, *args: object) -> None:
        super().__init__(msg, *args)
        self.col = col
        self.ln = ln
        self.msg = msg


@dataclass
class Position:
    file_name: str
    index: int = 0
    col: int = 0
    ln: int = 0

    def advance(self, current_character: str) -> None:
        self.index += 1
        self.col += 1

        if current_character == '\n':
            self.col = 1
            self.ln += 1

        return self


class Token:
    def __init__(self, token_type: str, col_start, col_end, ln, value: str = None) -> None:
        self.type = token_type
        self.value = value
        self.col_start = col_start
        self.col_end = col_end
        self.ln = ln

    def __str__(self) -> str:
        return f"Token<{self.type}:{self.value}>" if self.value is not None else f"Token<{self.type}>"

    def __repr__(self) -> str:
        return self.__str__()

    def match(self, _type, value) -> bool:
        return self.type == _type and self.value.lower() == value.lower()


class Lexer:
    def __init__(self, file_name: str, text: str) -> None:
        self.text = text
        self.position = Position(file_name=file_name)
        self.current_character = text[0] if text else None

    def advance(self) -> None:
        self.position.advance(self.current_character)
        self.current_character = self.text[self.position.index] if len(self.text) > self.position.index else None

    def tokens(self):
        tokens = []

        while self.current_character:
            match self.current_character:
                case '(':
                    tokens.append(Token(TN_LEFT_BRACKET, self.position.col, self.position.col + 1, self.position.ln))
                    self.advance()
                case ')':
                    tokens.append(Token(TN_RIGHT_BRACKET, self.position.col, self.position.col + 1, self.position.ln))
                    self.advance()
                case '[':
                    tokens.append(Token(TN_SQ_LEFT_BRACKET, self.position.col, self.position.col + 1, self.position.ln))
                    self.advance()
                case ']':
                    tokens.append(Token(TN_SQ_RIGHT_BRACKET, self.position.col, self.position.col + 1, self.position.ln))
                    self.advance()
                case '+':
                    tokens.append(Token(TN_PLUS, self.position.col, self.position.col + 1, self.position.ln))
                    self.advance()
                case '!':
                    self.advance()
                    if self.current_character == '=':
                        tokens.append(Token(TN_NEQ, self.position.col, self.position.col + 1, self.position.ln))
                        self.advance()
                    else:
                        raise UnExpectedCharacterError(
                            f'Expect character "=" but "{self.current_character}", recieved.\n'
                            f"{self.error_position_message(self.position)}"
                        )
                case '>':
                    position = copy(self.position)
                    self.advance()

                    if self.current_character == '=':
                        tokens.append(Token(TN_GRE, position.col, position.col + 2, position.ln))
                        self.advance()
                    else:
                        tokens.append(Token(TN_GR, position.col, position.col + 1, position.ln))
                case '<':
                    position = copy(self.position)
                    self.advance()

                    if self.current_character == '=':
                        tokens.append(Token(TN_LEE, position.col, position.col + 2, position.ln))
                        self.advance()
                    else:
                        tokens.append(Token(TN_LE, position.col, position.col + 1, position.ln))
                case '-':
                    position = copy(self.position)
                    self.advance()

                    if self.current_character and self.current_character == '>':
                        self.advance()
                        tokens.append(Token(TN_ARROW, position.col, position.col + 2, position.ln))
                    else:
                        tokens.append(Token(TN_MINUS, position.col, position.col + 1, position.ln))
                case '*':
                    tokens.append(Token(TN_MULTIPLY, self.position.col, self.position.col + 1, self.position.ln))
                    self.advance()
                case '/':
                    tokens.append(Token(TN_DIVIDE, self.position.col, self.position.col + 1, self.position.ln))
                    self.advance()
                case ',':
                    tokens.append(Token(TN_COMMA, self.position.col, self.position.col + 1, self.position.ln))
                    self.advance()
                case '=':
                    position = copy(self.position)
                    self.advance()

                    if self.current_character == '=':
                        tokens.append(Token(TN_EE, position.col, position.col + 2, position.ln))
                        self.advance()
                    else:
                        tokens.append(Token(TN_EQ, position.col, position.col + 1, position.ln))

                case '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | '0':
                    tokens.append(self.parse_num())
                case self.current_character if self.current_character in VERIABLE_LETTERS:
                    tokens.append(self.parse_identefier())
                case '"':
                    tokens.append(self.parse_string())
                case ' ' | '\n':
                    self.advance()
                case _:
                    raise LexerSyntaxError(
                        f"Syntax error:\n"
                        f"{self.error_position_message(self.position)}"
                    )

        tokens.append(Token(TN_EOF, self.position.col, self.position.col, self.position.ln))

        return tokens

    def error_position_message(self, position: Position) -> str:
        error_line = self.text.split('\n')[position.ln]
        return (
            f"{error_line}\n"
            f"{' ' * position.col}^\n"
            f"ln: {position.ln + 1} col: {position.col + 1}"
        )


    def parse_num(self) -> Token:
        number = ''
        with_dot = False
        col = self.position.col

        while self.current_character and self.current_character in "0123456789.":
            if self.current_character in "0123456789":
                number += self.current_character
                self.advance()
            elif self.current_character == '.' and not with_dot:
                with_dot = True
                number += self.current_character
                self.advance()

        return (
            Token(TN_FLOAT, col, self.position.col + 1, self.position.ln, float(number))
            if with_dot
            else Token(TN_INT, col, self.position.col + 1, self.position.ln, int(number))
        )

    def parse_string(self) -> Token:
        string_value = ''

        position = copy(self.position)
        self.advance()

        while self.current_character and self.current_character != '"':
            string_value += self.current_character
            self.advance()

        self.advance()

        return Token(TN_STRING, position.col, self.position.col, position.ln, string_value)


    def parse_identefier(self) -> Token:
        identefier = ''
        col = self.position.col

        while self.current_character and self.current_character in VERIABLE_LETTERS:
            identefier += self.current_character
            self.advance()

        if identefier.upper() in KEYWORDS:
            return Token(TN_KEYWORD, col, self.position.col + 1, self.position.ln, identefier.upper())

        return Token(TN_IDENTEFIER, col, self.position.col + 1, self.position.ln, identefier)



class ASTBase:
    pass

class VarNode(ASTBase):
    def __init__(self, name, expresion) -> None:
        self.name = name
        self.expresion = expresion

    def __repr__(self) -> str:
        return f"VarNode({self.name}={self.expresion})"

class VariableNode(ASTBase):
    def __init__(self, name) -> None:
        self.name = name

    def __repr__(self) -> str:
        return f"VariableNode({self.name})"

class NumberNode(ASTBase):
    def __init__(self, token) -> None:
        self.token = token

    def __repr__(self) -> str:
        return f"Number({self.token})"

class StringNode(ASTBase):
    def __init__(self, token) -> None:
        self.token = token

    def __repr__(self) -> str:
        return f"String({self.token})"

class UnarNode(ASTBase):
    def __init__(self, token_op, number_node) -> None:
        self.number_node = number_node
        self.token_op = token_op

    def __repr__(self) -> str:
        if self.token_op.type == TN_MINUS:
            return f"-{self.number_node}"
        return f"{self.number_node}"


class BinOpNode(ASTBase):
    def __init__(self, left_token, operation, right_token) -> None:
        self.left_token = left_token
        self.operation = operation
        self.right_token = right_token

    def __repr__(self) -> str:
        return f"( {self.left_token} {OPERATION_MAP[self.operation.type]} {self.right_token} )"


class BoolBinOpNode(ASTBase):
    def __init__(self, left_token, operation, right_token) -> None:
        self.left_token = left_token
        self.operation = operation
        self.right_token = right_token

    def __repr__(self) -> str:
        return f"( {self.left_token} {BOOL_OPERATION_MAP[self.operation.type]} {self.right_token} )"


class IFNode(ASTBase):
    def __init__(self, condition, then_block, elif_list, else_block) -> None:
        self.condition = condition
        self.then_block = then_block
        self.elif_list = elif_list
        self.else_block = else_block

    def __repr__(self) -> str:
        text = f"if {self.condition} then {self.then_block}"

        for cond, block in self.elif_list:
            text += f" elif {cond} then {block}"

        if self.else_block:
            text += f" else {self.else_block}"

        return text

class WhileNode(ASTBase):
    def __init__(self, condition, expression) -> None:
        self.condition = condition
        self.expression = expression

    def __repr__(self) -> str:
        return f"while {self.condition} then {self.expression}"

class FuncNode(ASTBase):
    def __init__(self, name, args, expression) -> None:
        self.name = name
        self.args = args
        self.expression = expression

    def __repr__(self) -> str:
        return f"function {self.name}"

class CallNode(ASTBase):
    def __init__(self, name, args) -> None:
        self.name = name
        self.args = args

    def __repr__(self) -> str:
        return f"function {self.name} ({self.args})"

class ForNode(ASTBase):
    def __init__(self, condition_start, condition_end, step, expression) -> None:
        self.condition_start = condition_start
        self.condition_end = condition_end
        self.step = step
        self.expression = expression

    def __repr__(self) -> str:
        return f"if {self.condition_start} to {self.condition_end} step {self.step} then {self.expression}"



class ArrNode(ASTBase):
    def __init__(self, items) -> None:
        self.items = items

    def __repr__(self) -> str:
        prefix = ''

        if len(self.items) > 3:
            prefix = ', ... '

        items = ", ".join([str(i) for i in self.items][:3])

        return f"[{items}{prefix}]"


class Parser:
    def __init__(self, tokens: list) -> None:
        self.tokens = tokens
        self.index = 0
        self.current_token = tokens[self.index] if tokens else None

    def parse(self):
        ast = self.expression()
        if self.current_token.type != TN_EOF:
            print(self.current_token)
            raise ParserSyntaxError("Expected some operation: +-/*", self.current_token.col_start, self.current_token.ln)
        return ast

    def advance(self):
        self.index += 1

        if len(self.tokens) > self.index:
            self.current_token = self.tokens[self.index]
        else:
            self.current_token = None

        return self.current_token

    def factor(self):
        token = self.current_token
        if token and token.type in [TN_INT, TN_FLOAT]:
            self.advance()
            return NumberNode(token)
        elif token and token.type == TN_STRING:
            self.advance()
            return StringNode(token)
        elif token and token.type == TN_LEFT_BRACKET:
            self.advance()
            expression = self.expression()

            if self.current_token.type != TN_RIGHT_BRACKET:
                raise ParserSyntaxError("Expected )", self.current_token.col_start, token.ln)

            self.advance()

            return expression
        elif token and token.type == TN_IDENTEFIER:
            call = self.call()

            if call:
                return call
        elif token and token.type == TN_SQ_LEFT_BRACKET:
            arr = self.arr()

            if arr: return arr

        raise ParserSyntaxError("Expected number or expression", token.col_start + 1, token.ln)

    def unar_factor(self):
        token = self.current_token
        if token and (token.type in [TN_PLUS, TN_MINUS]):
            self.advance()
            return UnarNode(token, self.unar_factor())

        return self.factor()

    def term(self):
        factor = self.unar_factor()

        while self.current_token and self.current_token.type in [TN_MULTIPLY, TN_DIVIDE]:
            operation = self.current_token
            self.advance()
            factor = BinOpNode(factor, operation, self.unar_factor())

        return factor

    def expression(self):
        if self.current_token and self.current_token.match(TN_KEYWORD, "VAR"):
            self.advance()

            if self.current_token.type == TN_IDENTEFIER:
                identifier = self.current_token
            else:
                raise ParserSyntaxError("Expected identifier", self.current_token.col_start + 1, self.current_token.ln)

            self.advance()

            if self.current_token.type != TN_EQ:
                raise ParserSyntaxError("Expected =", self.current_token.col_start + 1, self.current_token.ln)

            self.advance()

            expression = self.expression()

            if not expression:
                raise ParserSyntaxError("Expected expresion", self.current_token.col_start + 1, self.current_token.ln)

            return VarNode(identifier.value, expression)

        return self.if_statement() or self.while_statement() or self.for_statement() or self.func() or self.bool_expression()

    def arr(self) -> Token:
        items = []

        if self.current_token.type == TN_SQ_LEFT_BRACKET:
            self.advance()

            while self.current_token.type != TN_SQ_RIGHT_BRACKET:
                items.append(self.expression())

                if self.current_token.type == TN_COMMA:
                    self.advance()

            self.advance()

            return ArrNode(items)

        return None

    def bool_expression(self):
        if self.current_token and self.current_token.match(TN_KEYWORD, "NOT"):
            token = self.current_token
            self.advance()
            return UnarNode(token, self.bool_expression())
        simple_expression = self.simple_expression()

        if (
            self.current_token.type in [TN_GR, TN_GRE, TN_LE, TN_LEE, TN_EE, TN_NEQ]
            or self.current_token.match(TN_KEYWORD, "AND")
            or self.current_token.match(TN_KEYWORD, "OR")
        ):
            operation = self.current_token
            self.advance()
            simple_expression = BoolBinOpNode(simple_expression, operation, self.bool_expression())

        return simple_expression

    def func(self):
        if self.current_token and self.current_token.match(TN_KEYWORD, "FUNC"):
            self.advance()
            name = None

            if self.current_token and self.current_token.type == TN_IDENTEFIER:
                name = self.current_token.value
                self.advance()

            if self.current_token and self.current_token.type != TN_LEFT_BRACKET:
                raise ParserSyntaxError("Expected (", self.current_token.col_start + 1, self.current_token.ln)

            self.advance()

            args = []

            if self.current_token and self.current_token.type == TN_IDENTEFIER:
                args.append(self.current_token)
                self.advance()

                while self.current_token and self.current_token.type == TN_COMMA:
                    self.advance()
                    args.append(self.current_token)
                    self.advance()

            if self.current_token and self.current_token.type != TN_RIGHT_BRACKET:
                raise ParserSyntaxError("Expected ) or ,", self.current_token.col_start + 1, self.current_token.ln)

            self.advance()

            if self.current_token and self.current_token.type != TN_ARROW:
                raise ParserSyntaxError("Expected ) or ,", self.current_token.col_start + 1, self.current_token.ln)

            self.advance()

            expression = self.expression()

            return FuncNode(name, args, expression)

        return None

    def call(self):
        token = self.current_token
        identefier = token.value
        self.advance()
        if self.current_token.type != TN_LEFT_BRACKET:
            return VariableNode(identefier)

        self.advance()

        args = []

        while self.current_token and self.current_token.type != TN_RIGHT_BRACKET:
            args.append(self.expression())

            if self.current_token and self.current_token.type == TN_COMMA:
                self.advance()

        self.advance()

        return CallNode(token, args)

    def while_statement(self):
        if self.current_token and self.current_token.match(TN_KEYWORD, "WHILE"):
            self.advance()
            condition = self.expression()

            if self.current_token and not self.current_token.match(TN_KEYWORD, "THEN"):
                raise ParserSyntaxError("Expected THEN", self.current_token.col_start + 1, self.current_token.ln)

            self.advance()
            then_block = self.expression()

            return WhileNode(condition, then_block)

        return None

    def for_statement(self):
        if self.current_token and self.current_token.match(TN_KEYWORD, "FOR"):
            self.advance()
            condition_start = self.expression()
            step = 1

            if self.current_token and not self.current_token.match(TN_KEYWORD, "TO"):
                raise ParserSyntaxError("Expected TO", self.current_token.col_start + 1, self.current_token.ln)

            self.advance()
            condition_end = self.expression()

            if self.current_token and self.current_token.match(TN_KEYWORD, "STEP"):
                self.advance()
                step = self.expression()

            if self.current_token and not self.current_token.match(TN_KEYWORD, "THEN"):
                raise ParserSyntaxError("Expected THEN ...", self.current_token.col_start + 1, self.current_token.ln)

            self.advance()
            then_block = self.expression()

            return ForNode(condition_start, condition_end, step, then_block)

        return None

    def if_statement(self):
        condition = None
        then_block = None
        elif_list = []
        else_block = None

        if self.current_token and self.current_token.match(TN_KEYWORD, "IF"):
            self.advance()
            condition = self.expression()

            if self.current_token and not self.current_token.match(TN_KEYWORD, "THEN"):
                raise ParserSyntaxError("Expected THEN", self.current_token.col_start + 1, self.current_token.ln)

            self.advance()
            then_block = self.expression()

            while self.current_token and self.current_token.match(TN_KEYWORD, "ELIF"):
                self.advance()
                condition_elif = self.expression()

                if self.current_token and not self.current_token.match(TN_KEYWORD, "THEN"):
                    raise ParserSyntaxError("Expected THEN", self.current_token.col_start + 1, self.current_token.ln)

                self.advance()
                then_block_elif = self.expression()

                elif_list.append((condition_elif, then_block_elif))

            if self.current_token and self.current_token.match(TN_KEYWORD, "ELSE"):
                self.advance()
                else_block = self.expression()

            return IFNode(condition, then_block, elif_list, else_block)

        return None

    def simple_expression(self):
        term = self.term()

        while (
            self.current_token
            and self.current_token.type in [TN_PLUS, TN_MINUS]
        ):
            operation = self.current_token
            self.advance()
            term = BinOpNode(term, operation, self.term())

        return term

class VariableTable:
    def __init__(self, parent=None):
        self.data = {}
        self.parent = parent

    def get(self, name):
        value = self.data.get(name)

        if value is None and self.parent:
            value = self.parent.get(name)

        return value

    def set(self, name, value):
        self.data[name] = value

    def delete(self, name):
        del self.data[name]


class Value:
    def multiply(self, node):
        raise IllegalOperationException(f"unsupport operation * for {self} and {node}")


    def devide(self, node):
        raise IllegalOperationException(f"unsupport operation / for {self} and {node}")

    def add(self, node):
        raise IllegalOperationException(f"unsupport operation + for {self} and {node}")

    def subtract(self, node):
        raise IllegalOperationException(f"unsupport operation - for {self} and {node}")

    def un_minus(self):
        raise IllegalOperationException(f"unsupport operation - for {self}")

    def un_plus(self):
        raise IllegalOperationException(f"unsupport operation + for {self}")

    def gt(self, node):
        raise IllegalOperationException(f"unsupport operation > for {self} and {node}")

    def gte(self, node):
        raise IllegalOperationException(f"unsupport operation >= for {self} and {node}")

    def lt(self, node):
        raise IllegalOperationException(f"unsupport operation < for {self} and {node}")

    def lte(self, node):
        raise IllegalOperationException(f"unsupport operation <= for {self} and {node}")

    def eq(self, node):
        raise IllegalOperationException(f"unsupport operation == for {self} and {node}")

    def no_eq(self, node):
        raise IllegalOperationException(f"unsupport operation != for {self} and {node}")

    def is_true(self):
        return 1

class Number(Value):
    def __init__(self, value) -> None:
        self.value = value

    def multiply(self, node):
        if isinstance(node, Number):
            return Number(node.value * self.value)

        raise IllegalOperationException(f"unsupport operation * for number and {node}")


    def devide(self, node):
        if isinstance(node, Number):
            return Number(self.value / node.value)

        raise IllegalOperationException(f"unsupport operation / for number and {node}")

    def add(self, node):
        if isinstance(node, Number):
            return Number(node.value + self.value)

        raise IllegalOperationException(f"unsupport operation + for number and {node}")

    def subtract(self, node):
        if isinstance(node, Number):
            return Number(self.value - node.value)

        raise IllegalOperationException(f"unsupport operation - for number and {node}")

    def un_minus(self):
        return Number(- self.value)

    def un_plus(self):
        return self

    def gt(self, node):
        if isinstance(node, Number):
            return Number(self.value > node.value)

        raise IllegalOperationException(f"unsupport operation > for number and {node}")

    def gte(self, node):
        if isinstance(node, Number):
            return Number(self.value >= node.value)

        raise IllegalOperationException(f"unsupport operation >= for number and {node}")

    def lt(self, node):
        if isinstance(node, Number):
            return Number(self.value < node.value)

        raise IllegalOperationException(f"unsupport operation < for number and {node}")

    def lte(self, node):
        if isinstance(node, Number):
            return Number(self.value <= node.value)

        raise IllegalOperationException(f"unsupport operation <= for number and {node}")

    def eq(self, node):
        if isinstance(node, Number):
            return Number(node.value == self.value)

        raise IllegalOperationException(f"unsupport operation == for number and {node}")

    def no_eq(self, node):
        if isinstance(node, Number):
            return Number(node.value != self.value)

        raise IllegalOperationException(f"unsupport operation != for number and {node}")

    def is_true(self):
        return 1 if self.value != 0 else 0

    def __repr__(self) -> str:
        return str(self.value)


class String(Value):
    def __init__(self, value) -> None:
        self.value = value

    def multiply(self, node):
        if isinstance(node, Number):
            return String(self.value * node.value)

        raise IllegalOperationException(f"unsupport operation * for string and {node}")

    def add(self, node):
        if isinstance(node, String):
            return String(self.value + node.value)

        raise IllegalOperationException(f"unsupport operation + for string and {node}")

    def is_true(self):
        return 1 if len(self.value) != 0 else 0

    def __repr__(self) -> str:
        return str(self.value)


class ArrayValue:
    def __init__(self, nodes) -> None:
        self.nodes = nodes

    def multiply(self, node):
        if isinstance(node, ArrayValue):
            return ArrayValue([*self.nodes, *node.nodes])

        raise IllegalOperationException(f"unsupport operation * for {self} and {node}")

    def devide(self, node):
        raise IllegalOperationException(f"unsupport operation / for {self} and {node}")

    def add(self, node):
        return ArrayValue([*self.nodes, node])

    def subtract(self, node):
        raise IllegalOperationException(f"unsupport operation - for {self} and {node}")

    def un_minus(self):
        raise IllegalOperationException(f"unsupport operation - for {self}")

    def un_plus(self):
        raise IllegalOperationException(f"unsupport operation + for {self}")

    def gt(self, node):
        raise IllegalOperationException(f"unsupport operation > for {self} and {node}")

    def gte(self, node):
        raise IllegalOperationException(f"unsupport operation >= for {self} and {node}")

    def lt(self, node):
        raise IllegalOperationException(f"unsupport operation < for {self} and {node}")

    def lte(self, node):
        raise IllegalOperationException(f"unsupport operation <= for {self} and {node}")

    def eq(self, node):
        raise IllegalOperationException(f"unsupport operation == for {self} and {node}")

    def no_eq(self, node):
        raise IllegalOperationException(f"unsupport operation != for {self} and {node}")

    def is_true(self):
        return 1 if len(self.items) else 0

    def __repr__(self) -> str:
        return f'arr {self.nodes}'

    def __repr__(self) -> str:
        items = ", ".join([str(i) for i in self.nodes])

        return f"[{items}]"


class Function(Value):
    def __init__(self, name, args, body) -> None:
        self.name = name or 'anon'
        self.args = args
        self.body = body

    def __repr__(self) -> str:
        return f'function {self.name}'

    def execute(self, args_values, parent_varialbe):
        if len(self.args) != len(args_values):
            raise RuntimeError(f"wrong number of arguments. expected {len(self.args)} but {len(args_values)} received")

        inner_variable =  VariableTable(parent_varialbe)

        for name, value in zip(self.args, args_values):
            inner_variable.set(name, value)

        result = Interpreter(inner_variable).visit(self.body)
        return result


class Interpreter:
    def __init__(self, variables):
        self.variables = variables

    def visit(self, node):
        method = getattr(self, f"visit_{node.__class__.__name__}", self.visit_no_method)
        return method(node)

    def visit_NumberNode(self, node):
        return Number(node.token.value)

    def visit_StringNode(self, node):
        return String(node.token.value)

    def visit_UnarNode(self, node):
        if node.token_op.type == TN_MINUS:
            return self.visit(node.number_node).un_minus()
        elif node.token_op.type == TN_PLUS:
            return self.visit(node.number_node).un_plus()
        elif node.token_op.match(TN_KEYWORD, "NOT"):
            return Number(0) if self.visit(node.number_node).is_true() else Number(1)

        raise Exception(f"no handle unar operation type {node}")

    def visit_VarNode(self, node):
        value = self.visit(node.expresion)
        self.variables.set(node.name, value)
        return value

    def visit_IFNode(self, node):
        condition = self.visit(node.condition)

        if condition.is_true():
            return self.visit(node.then_block)

        for cond_obj, expresion_obj in node.elif_list:
            condition = self.visit(cond_obj)

            if condition.is_true():
                return self.visit(expresion_obj)

        if node.else_block:
            return self.visit(node.else_block)

        return None

    def visit_WhileNode(self, node):
        while self.visit(node.condition).is_true():
            self.visit(node.expression)

    def visit_ForNode(self, node):
        condition_start = self.visit(node.condition_start)
        condition_end = self.visit(node.condition_end)
        step = node.step if isinstance(node.step, int) else self.visit(node.step).value
        for i in range(condition_start.value, condition_end.value, step):
            self.visit(node.expression)

    def visit_VariableNode(self, node):
        value = self.variables.get(node.name)

        if value is None:
            print(self.variables)
            raise Exception(f"value '{node.name}' is not specified")

        return value

    def visit_CallNode(self, node):
        value = self.variables.get(node.name.value)

        if value is None:
            raise Exception(f"value '{node.name}' is not specified")

        res =  value.execute([self.visit(i) for i in node.args], self.variables)
        return res

    def visit_FuncNode(self, node):
        function_obj = Function(node.name, [i.value for i in node.args], node.expression)
        if node.name:
            self.variables.set(node.name, function_obj)

        return function_obj

    def visit_BinOpNode(self, node):
        left = self.visit(node.left_token)
        right = self.visit(node.right_token)

        if node.operation.type == TN_DIVIDE:
            return left.devide(right)
        elif node.operation.type == TN_MULTIPLY:
            return left.multiply(right)
        elif node.operation.type == TN_MINUS:
            return left.subtract(right)
        elif node.operation.type == TN_PLUS:
            return left.add(right)

    def visit_ArrNode(self, node):
        items = []

        for i in node.items:
            items.append(self.visit(i))

        return ArrayValue(items)

    def visit_BoolBinOpNode(self, node):
        left = self.visit(node.left_token)
        right = self.visit(node.right_token)

        if node.operation.type == TN_GR:
            return left.gt(right)
        elif node.operation.type == TN_GRE:
            return left.gte(right)
        elif node.operation.type == TN_LE:
            return left.lt(right)
        elif node.operation.type == TN_LEE:
            return left.lte(right)
        elif node.operation.type == TN_EE:
            return left.eq(right)
        elif node.operation.type == TN_NEQ:
            return left.no_eq(left)
        elif node.operation.match(TN_KEYWORD, "AND"):
            return Number(1) if left.is_true() and right.is_true() else Number(0)
        elif node.operation.match(TN_KEYWORD, "OR"):
            return Number(1) if left.is_true() or right.is_true() else Number(0)

        raise Exception(f"no handle operation type {node.operation.type}")

    def visit_no_method(self, node):
        raise Exception(f"no found visit method for node {node.__class__.__name__}")
