#!/usr/bin/env python
# -*- coding: utf-8 -*-

import codecs
import re

posFile = 'POS.txt'
posList = {}
newPosList = {}

negFile = 'NEG.txt'
negList = {}
newNegList = {}

swapwordFile = 'swapword.txt'
swapwordList = {}
downwordFile = 'downword.txt'
downwordList = {}
newPosFile = 'NEW_POS.txt'
newNegFile = 'NEW_NEG.txt'


# read positive word from file
def readPositiveWord():
	print 'readPositiveWord'
	f = codecs.open(posFile, encoding='utf-8-sig')
	for line in f:
		# print line
		line = line.replace('\n', '')
		line = line.replace('\r', '')
		# line = line.replace(' ', '_')
		word = re.split(' ', line)[0]
		posList[word] = 1
		print word

# # read negative word from file
def readNegativeWord():
	f = codecs.open(negFile, encoding='utf-8-sig')
	for line in f:
		# print line
		line = line.replace('\n', '')
		line = line.replace('\r', '')
		# line = line.replace(' ', '_')
		word = re.split(' ', line)[0]
		negList[word] = 1
		print word

# read swapword from file
def readSwapWord():
	f = codecs.open(swapwordFile, encoding='utf-8-sig')
	for line in f:
		# print line
		line = line.replace('\n', '')
		line = line.replace('\r', '')
		# line = line.replace(' ', '_')
		word = re.split(' ', line)[0]
		swapwordList[word] = 1

def readDownWord():
	f = codecs.open(downwordFile, encoding='utf-8-sig')
	for line in f:
		line = line.replace('\n', '')
		line = line.replace('\r', '')
		line = line.replace(' ', '_')
		word = re.split(' ', line)[0]
		downwordList[word] = 1	

if __name__ == '__main__':
	readSwapWord()
	readDownWord()
	readPositiveWord()
	readNegativeWord()

	for posword in posList:
		print posword
		newPosList[posword] = 1
		for swapword in swapwordList:
			newNegList[swapword + '_' + posword] = 1
		for downword in downwordList:
			newNegList[posword + '_' + downword] = 1

	for negword in negList:
		newNegList[negword] = 1
		for swapword in swapwordList:
			newPosList[swapword + '_' + negword] = 1
		for downword in downwordList:
			newPosList[posword + '_' + downword] = 1


	f = codecs.open(newPosFile, 'w', encoding='utf-8-sig')
	for posword in newPosList:
		f.write(posword + '\n')
	f.close()

	f = codecs.open(newNegFile, 'w', encoding='utf-8-sig')
	for negword in newNegList:
		f.write(negword + '\n')
	f.close()