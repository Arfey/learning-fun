class BaseField:
	pass

class TextField(BaseField):
	constructor = str

class IntegerField(BaseField):
	constructor = int

class ModelMeta(type):
	def __new__(cls, name, bases, args, **kwargs):
		print(name, bases, args, kwargs)
		new_args = []
		fields = []
		for arg in args:
			if isinstance(arg, BaseField):
				fields.append(arg)
			else:
				new_args.append(arg)
		new_class = super().__new__(cls, name, bases, args)
		return new_class

class Model(metaclass=ModelMeta):
	pass

class Book(Model):
	title = TextField()
	page_count = IntegerField()


print(Book())
