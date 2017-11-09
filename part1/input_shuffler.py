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
	wizard_list = str_to_word_arr(text, ' ')
	f.close()
	return wizard_list

def write_to_file(filename, count, list, mode='w'):
	f = open(filename, mode)
	f.write(str(count) + "\n")
	f.write(word_arr_to_str(list[:count], ' ') + '\n')
	f.close()

if __name__ == '__main__':
	wizard_list = file_to_list(WIZARD_LIST)
	shuffle(wizard_list)
	write_to_file(INPUT20, 20, wizard_list)
	shuffle(wizard_list)
	write_to_file(INPUT35, 35, wizard_list)
	shuffle(wizard_list)
	write_to_file(INPUT50, 50, wizard_list)


