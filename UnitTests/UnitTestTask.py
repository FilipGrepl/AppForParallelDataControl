#!/usr/bin/python

# test program - work with one input file and one output file

import sys
import argparse
import time
import os.path


def lineCounter(filename):
    file = open(filename, "r")
    line_count = 0
    for line in file:
        if line != "\n":
            line_count += 1
    file.close()
    return line_count

parser = argparse.ArgumentParser(description='Test success task')
parser.add_argument('-in', '--in', required=True)
parser.add_argument('-out', '--out', required=True)
parser.add_argument('-secondOut', '--secondOut', required=False)
parser.add_argument('-logFilePath', '--logFilePath', required=False)
parser.add_argument('-errLogFilePath', '--errlogFilePath', required=False)
parser.add_argument('-sleep', '--sleep', required=True, type=int)
parser.add_argument('-createError', '--createError', required=False, action='store_true')

# validation of existing in/out path is checked in Secondary node java program

args = parser.parse_args()

print(args)

time.sleep(args.sleep)


if "in2.out" in args.out or "in3.out" in args.out:
    filename = args.out[0:args.out.rfind("/")]+"/temp.tmp"
    f = open(filename, mode="a")
    f.write(args.out+"\n")
    f.close()
    if args.createError and not (lineCounter(filename) >= 3):
        sys.exit(0)

f = open(args.out, mode="w")
f.write("This is content of output")
f.close()

if args.secondOut:
    f = open(args.secondOut, mode="w")
    f.write("This is content of second output")
    f.close()

if args.logFilePath:
    f = open(args.logFilePath, mode="w")
    f.write("This is awesome content of log file.")
    f.close()

if args.errlogFilePath:
    f = open(args.errlogFilePath, mode="w")
    f.write("This is content of error log file.")
    f.close()