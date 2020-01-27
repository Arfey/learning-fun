#include <stdio.h>

void select_sort(int arr[], long length)
{
    int min_val;
    int min_index;
    int replace_value;

    for (int i = 0; i <= length - 1; i++) {
        min_val = arr[i];
        min_index = i;

        for (int j = i + 1; j < length; j++) {
            if (arr[j] < min_val) {
                min_val = arr[j];
                min_index = j;
            }
        }

        replace_value = arr[i];

        arr[i] = min_val;
        arr[min_index] = replace_value;
    }
}

void printArrey(int arr[], long length) {
    printf("\narr<");
    for (int i = 0; i < length - 1; i++) {
        printf("%d, ", arr[i]);
    }
    printf("%d>\n", arr[length - 1]);
}

int main()
{
    int arr[] = {1, 3, 5, 10, 11, 2, 4, -1};

    printf("\nSelection sort algorithm:\n");

    printArrey(arr, sizeof(arr) / sizeof(arr[0]));

    select_sort(arr, sizeof(arr) / sizeof(arr[0]));

    printArrey(arr, sizeof(arr) / sizeof(arr[0]));
}
