import time
import csv

def initialize_matriz(size):
    matrix_a = []
    matrix_b = []
    matrix_c = []
    for i in range(size):
        for j in range(size):
            matrix_a.append(1.0)
            matrix_c.append(0)

    for i in range(size):
        for j in range(size):
            matrix_b.append(i + 1.0)

    return (matrix_a, matrix_b, matrix_c)


def mult_matrix(size):
    (ma, mb, mc) = initialize_matriz(size)
    init = time.process_time()
    for i in range(size):
        for j in range(size):
            for k in range(size):
                mc[i*size + j] += ma[i*size + k] * mb[k*size+j]
    func_time = (time.process_time() - init) 
    print("Exec time: %fs" % func_time)

    print_10matrix(mc)
    return func_time

def mult_line_matrix(size):
    (ma, mb, mc) = initialize_matriz(size)
    init = time.process_time()
    for i in range(size):
        for k in range(size):
            for j in range(size):
                mc[i*size + j] += ma[i*size + k] * mb[k*size+j]
    func_time = (time.process_time() - init) 
    print("Exec time: %fs" % func_time)

    print_10matrix(mc)
    return func_time

def mult_matrix_block(size, block_size):
    (ma, mb, mc) = initialize_matriz(size)
    init = time.process_time()

    for l in range(0, size, block_size):
        for m in range(0, size, block_size):
            for n in range(0, size, block_size):
                for i in range(l, min(l+block_size, size)):
                    for k in range(n, min(n + block_size, size)):
                        for j in range(m, min(m + block_size, size)):
                            mc[i*size + j] += ma[i*size + k] * mb[k*size+j]

    func_time = (time.process_time() - init) 
    print("Exec time: %fs" % func_time)

    print_10matrix(mc)
    return func_time

def print_10matrix(matrix):
    for i in range(min(10, len(matrix))):
        print(matrix[i], end = " ")
    print("\n")

def results():
    f = open("g01/assign1/src/Results_python.csv", 'w')

    writer = csv.writer(f, delimiter = ";")

    headers = ["Matrix Size", "Time"]

    writer.writerow(headers)

    for i in range(600, 3100, 400):
        time = mult_matrix(i)
        row = [str(i), str(time)]
        writer.writerow(row)

    writer.writerow("")
    writer.writerow(headers)

    for i in range(600, 3100, 400):
        time = mult_line_matrix(i)
        row = [str(i), str(time)]
        writer.writerow(row)
    

def block_results():
    f = open("g01/assign1/src/Results_block_python.csv", 'w')

    writer = csv.writer(f, delimiter = ";")

    headers = ["Matrix Size", "Block Size", "Time"]
    writer.writerow(headers)

    for i in range(20, 30):
        for j in range(10, 13):
            time = mult_matrix_block(i, j)
            row = [str(i), str(j), str(time)]
            writer.writerow(row)

while True:
    print("0. Exit")
    print("1. Multiplication")
    print("2. Line Multiplication")
    print("3. Block Multiplication")
    print("4. Save Results")
    print("Choose option: ")

    option = int(input())
    if(option == 0):
        break
    elif(option == 4):
        results()
        break

    print("Matrix size:", end = " ")
    m_size = int(input())

    if option == 1:
        m = mult_matrix(m_size)
    elif option == 2:
        m = mult_line_matrix(m_size)
    elif option == 3:
        print("Block size: ")
        b_size = int(input())
        m = mult_matrix_block(m_size, b_size)
    
    print()

