import time

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
    return mc

def mult_line_matrix(size):
    (ma, mb, mc) = initialize_matriz(size)
    init = time.process_time()
    for i in range(size):
        for k in range(size):
            for j in range(size):
                mc[i*size + j] += ma[i*size + k] * mb[k*size+j]
    func_time = (time.process_time() - init) 
    print("Exec time: %fs" % func_time)
    return mc

def mult_matrix_block(size, block_size):
    (ma, mb, mc) = initialize_matriz(size)
    init = time.process_time()

    for l in range(int(size/block_size)):
        for m in range(int(size/block_size)):
            for n in range(int(size/block_size)):
                for i in range(l*block_size, (l+1)*block_size):
                    for k in range(n*block_size, (n+1)*block_size):
                        for j in range(m*block_size, (m+1)*block_size):
                            mc[i*size + j] += ma[i*size + k] * mb[k*size+j]

    func_time = (time.process_time() - init) 
    print("Exec time: %fs" % func_time)
    return mc

def print_10matrix(matrix):
    for i in range(min(10, len(matrix))):
        print(matrix[i], end = " ")

while True:
    print("0. Exit")
    print("1. Multiplication")
    print("2. Line Multiplication")
    print("3. Block Multiplication")
    print("Choose option: ")

    option = int(input())
    if(option == 0):
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
    print_10matrix(m)
    print()

