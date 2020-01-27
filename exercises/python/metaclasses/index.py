def print_meta(class_name, class_parents, class_args):
	"""
	This function use as metaclass for all classes from current file
	and just print information that put for create class.
	"""
	print(class_name, class_parents, class_args)
	return type(class_name, class_parents, class_args)



class A(metaclass=print_meta):
	a = 1


