import random


INPUT20 = "./inputs/input20.in"
INPUT35 = "./inputs/input35.in"
INPUT50 = "./inputs/input50.in"



def str_to_word_arr(line, delimeter):
	return line.split(delimeter)

def word_arr_to_str(words, delimeter):
	return delimeter.join(words)

def file_to_list(filename):
	try:
		f = open(filename)
		count = f.readline()
		text = f.readline().rstrip()
		lst = str_to_word_arr(text, ' ')
		f.close()
		return lst
	except IOError:
		print("Couldn't open", filename)

def write_to_file(filename, count, text, mode='a'):
	try:
		f = open(filename, mode)
		f.write(str(count) + "\n")
		f.write(text)
		f.close()
	except IOError:
		print("Couldn't open", filename)

def generate_random_constraints_mean(names):
	used_constraints = set()
	mean = len(names) / 2
	epsilon = .5
	for i in range(len(names) * 7):
		while True:
			first = random.randrange(0,len(names))
			b = [first]
			while True:
				second = random.randrange(0,len(names))
				if second not in b:
					b += [second]
					break
			while True:
				third = random.randrange(0,len(names))
				if third not in b:
					b += [third]
					break
			b = sorted(b)
			
			avg = sum(b)/float(len(b))

			if avg >= mean - epsilon and avg <= mean + epsilon:
				a = random.random()
	
				lst = None
				if a < .25:
					lst = (b[0], b[1], b[2])
				elif a >= .25 and a < .5:
					lst = (b[1], b[0], b[2])
				elif a >= .5 and a < .75:
					lst = (b[2], b[1], b[0])
				else:
					lst = (b[1], b[2], b[0])
	
				permuted_lst = (lst[1], lst[0], lst[2])
	
				if lst not in used_constraints and permuted_lst not in used_constraints:
					used_constraints.add(lst)
					break
	return used_constraints


def generate_random_constraints_k(names):
	used_constraints = set()
	for i in range(len(names)):
		for k in range(7):
			while True:
				left = False
	
				if i > 2 and i < len(names) - 2:
					a = random.random()
					if a < .5:
						left = True
				else:
					if i >= len(names) - 2:
						left = True
	
				if left:
					while True:
						first = random.randrange(0,i)
						b = [first]
						break
					while True:
						second = random.randrange(0,i)
						if second not in b:
							b += [second]
							break
					b += [i]
				else:
					while True:
						first = random.randrange(i + 1,len(names))
						b = [first]
						break
					while True:
						second = random.randrange(i + 1, len(names))
						if second not in b:
							b += [second]
							break
					b += [i]
	
				a = random.random()
	
				lst = None
				if a < .5:
					lst = (b[0], b[1], b[2])
				else:
					lst = (b[1], b[0], b[2])
					
		
				permuted_lst = (lst[1], lst[0], lst[2])
		
				if lst not in used_constraints and permuted_lst not in used_constraints:
					used_constraints.add(lst)
					break
	return used_constraints

def generate_random_constraints_averaged_and_k(names):
	used_constraints = set()
	mean = len(names) / 2
	epsilon = 2

	for i in range(len(names)):
		for k in range(7):
			while True:
				left = False
	
				if i > 2 and i < len(names) - 2:
					a = random.random()
					if a < .5:
						left = True
				else:
					if i >= len(names) - 2:
						left = True
	
				if left:
					while True:
						first = random.randrange(0,i)
						b = [first]
						break
					while True:
						second = random.randrange(0,i)
						if second not in b:
							b += [second]
							break
					b += [i]
				else:
					while True:
						first = random.randrange(i + 1,len(names))
						b = [first]
						break
					while True:
						second = random.randrange(i + 1, len(names))
						if second not in b:
							b += [second]
							break
					b += [i]
		
				avg = sum(b)/float(len(b))
				if avg >= mean - epsilon and avg <= mean + epsilon:
				
					a = random.random()
		
					lst = None
					if a < .5:
						lst = (b[0], b[1], b[2])
					else:
						lst = (b[1], b[0], b[2])
					
		
					permuted_lst = (lst[1], lst[0], lst[2])
		
					if lst not in used_constraints and permuted_lst not in used_constraints:
						used_constraints.add(lst)
						break
	return used_constraints

def constraints_to_str(constraints, names):
	stri = ""
	for c in constraints:
		line = names[c[0]] + " " + names[c[1]] + " " + names[c[2]] + "\n"
		stri += line
	return stri

def build(filename, fn):
	names = file_to_list(filename)
	constraints = fn(names)
	write_to_file(filename, len(constraints), constraints_to_str(constraints, names))

if __name__ == '__main__':
	build(INPUT20, lambda x: generate_random_constraints_averaged_and_k(x))
	build(INPUT35, lambda x: generate_random_constraints_mean(x))
	build(INPUT50, lambda x: generate_random_constraints_k(x))
	

	

