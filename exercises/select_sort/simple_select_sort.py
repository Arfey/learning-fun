def select_sort(arr):
    """
    The function provide the simple and not optimistic algorithm
    for sortering array (Selection sort).

    Big O:

        O(n^2)
    """
    for current_index in range(len(arr) - 1):
        min_index = current_index

        for index in range(current_index + 1, len(arr)):
            if arr[min_index] > arr[index]:
                min_index = index
        
        arr[current_index], arr[min_index] = arr[min_index], arr[current_index]

    return arr


if __name__ == '__main__':
    print("\nSelection sort algorithm:\n")

    arr = [1, 4, 3, 5, 7, 2, 4, 1, 2, 3, 5]
    
    print(arr)
    print(select_sort(arr), '\n')
