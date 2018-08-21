#!/bin/sh
# This file is created to shorten the push master command for you
git push origin HEAD:i/$(whoami)/$(git rev-parse --abbrev-ref HEAD)
