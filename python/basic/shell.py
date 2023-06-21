from basic import Lexer, LexerSyntaxError, Parser, ParserSyntaxError, Interpreter, VariableTable, Number

global_variables = VariableTable()
global_variables.set('True', Number(1))
global_variables.set('False', Number(0))

try:
    while True:
        text = input('basic> ')
        tokens = Lexer('<stdin>', text).tokens()
        # print(tokens)
        ast = Parser(tokens).parse()
        # print(ast)
        print(Interpreter(global_variables).visit(ast))
except KeyboardInterrupt:
    print('\nexit ...')
except LexerSyntaxError as e:
    print(e.args[0])
except ParserSyntaxError as e:
    error_line = text.split('\n')[e.ln]
    print(
        f"Syntax error:\n"
        f"{error_line}\n"
        f"{' ' * (e.col - 1)}^\n"
        f"{e.msg}\n"
        f"ln: {e.ln + 1} col: {e.col + 1}"
    )
