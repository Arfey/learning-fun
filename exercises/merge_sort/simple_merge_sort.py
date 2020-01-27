import pprint
import timeit
import random


def merge_sort(arr):
	"""
	The simple implementation of the merge sort algorithm.
	"""
	if len(arr) is 1:
		return arr

	middle_index = len(arr) // 2 

	first_arr = merge_sort(arr[0:middle_index])
	secound_arr = merge_sort(arr[middle_index:])

	result_arr = []
	secound_step = 0

	for item in first_arr:
		new_arr = secound_arr[secound_step:]
		if (len(new_arr)):
			is_append = False
			for secound_item in new_arr:
				if secound_item < item:
					result_arr.append(secound_item)
					secound_step += 1
					continue
				else:
					result_arr.append(item)
					is_append = True
					break
			if not is_append:
				result_arr.append(item)
		else:
			result_arr.append(item)
	
	new_arr = secound_arr[secound_step:]

	if (len(new_arr)):
		result_arr.extend(new_arr)

	return result_arr


if __name__ == '__main__':
	print("The merge sort algorithm")
	arr = [1, 3, 4, 7, 3, 5, 3, 8, 2, 1]
	
	new_arr = merge_sort(arr)
	print("Start:")
	pprint.pprint(arr)
	print("Result:")
	pprint.pprint(new_arr)

	# test a big random list
	big_list = [random.randint(0, 100) for _ in range(100)]
	big_list_sorted_by_merge = merge_sort(big_list)
	big_list_sorted_by_sorted = sorted(big_list)
	for x, y in zip(big_list_sorted_by_merge, big_list_sorted_by_sorted):
		assert x == y

	# time performance

	time = timeit.Timer(lambda: merge_sort(arr)).repeat(repeat=10, number=1)
	print("\nTime performance (10 items): ", sum(time) / len(time))

	time = timeit.Timer(lambda: merge_sort(big_list)).repeat(repeat=10, number=1) 
	print("\nTime performance (100 items): ", sum(time) / len(time))

