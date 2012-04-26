#!/usr/bin/env python
import sys
base_path = sys.argv.pop()
sys.path.insert(0, base_path)
from fix_getpass import fixGetpass
import os
from django.core.management import execute_manager

try:
  from runpy import run_module
except ImportError:
  from runpy_compat import run_module

manage_file = os.getenv('PYCHARM_DJANGO_MANAGE_MODULE')
if not manage_file:
    manage_file = 'manage'

if __name__ == "__main__":
    fixGetpass()
    run_module(manage_file, None, '__main__', True)

