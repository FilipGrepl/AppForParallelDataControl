#!/usr/bin/python

# test program - work with one input file and one output file

import sys
import argparse
import time
import os.path
import random

parser = argparse.ArgumentParser(description='Test success task')
parser.add_argument('-in', '--in', required=True)
parser.add_argument('-out', '--out', required=True)
parser.add_argument('-secondOut', '--secondOut', required=False)
parser.add_argument('-logFilePath', '--logFilePath', required=False)
parser.add_argument('-errLogFilePath', '--errlogFilePath', required=False)
parser.add_argument('-sleep', '--sleep', required=True, type=int)

# validation of existing in/out path is checked in Secondary node java program

args = parser.parse_args()

print(args)

time.sleep(random.randint(0,args.sleep))

if "in2.out" in args.out or "in4.out" in args.out:
    time.sleep(60)

#if "in2.out" in args.out or "in1.out" in args.out:
f = open(args.out, mode="w")
f.write("This is content of output")
f.close()

if args.secondOut:
    f = open(args.secondOut, mode="w")
    f.write("This is content of second output")
    f.close()

if args.logFilePath:
    f = open(args.logFilePath, mode="w")
    f.write("This is content of log file.")
    f.close()

if args.errlogFilePath:
    f = open(args.errlogFilePath, mode="w")
    f.write("This is content of error log file.")
    f.close()