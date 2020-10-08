#!/bin/bash

rsync -av --delete --exclude .git data/ web1.topobyte.de:/home/z/webdata/covid-plz/data/
