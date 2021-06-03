def test_recursion(k):
    if(k > 0):
        result = k + test_recursion(k-1)
        print(result)
    else:
        result = 0
    return result
print("\n\nRecursion Example Results")
test_recursion(10)

def recur_fibo(n):
    if n <= 1:
       return n
    else:
        return(recur_fibo(n-1) + recur_fibo(n-2))
nterms = 20

if nterms <= 0:
    print("Please enter a positive integer")
else:
    print("Fibonacci sequence:")
    for i in range(nterms):
        print(recur_fibo(i))
