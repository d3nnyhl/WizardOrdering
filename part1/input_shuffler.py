from random import shuffle

WIZARD_LIST = "./wizard_list.txt"
INPUT20 = "./inputs/input20.in"
INPUT35 = "./inputs/input35.in"
INPUT50 = "./inputs/input50.in"

def str_to_word_arr(line, delimeter):
	return line.split(delimeter)

def word_arr_to_str(words, delimeter):
	return delimeter.join(words)

def file_to_list(filename):
	f = open(filename)
	text = f.readline()
	lst = str_to_word_arr(text, ' ')
	f.close()
	return lst

def write_to_file(filename, count, lst, mode='w'):
	f = open(filename, mode)
	f.write(str(count) + "\n")
	f.write(word_arr_to_str(lst[:count], ' ') + '\n')
	f.close()

if __name__ == '__main__':
	lst = file_to_list(WIZARD_LIST)
	shuffle(lst)
	write_to_file(INPUT20, 20, lst)
	shuffle(lst)
	write_to_file(INPUT35, 35, lst)
	shuffle(lst)
	write_to_file(INPUT50, 50, lst)


