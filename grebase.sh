#!/bin/sh
# Update develop from origin and optionally rebase current branch
# To specify the develop branch:
#
# ./grebase.sh dev
#
cur_branch=$(git rev-parse --abbrev-ref HEAD)
cur_remote=$(git remote | grep origin | tail -n 1)
if [ -z "$cur_remote" ]
  then
    cur_remote=$(git remote | tail -n 1)
fi
if [ -z "$1" ]
    then
        cur_dev=$(git branch | grep develop | tail -n 1)
    else
        cur_dev=$1
fi
if [ -z "$cur_dev" ]
    then
	    echo "Failed to get develop!" 1>&2
	exit 1
fi
git checkout $cur_dev -- site.properties
git checkout $cur_dev
if [ "$?" = "0" ]; then
	echo "Pulling from remote develop ..."
	git pull $cur_remote $cur_dev
	# git fetch $cur_remote
	while true; do
    read -p "Do you wish to rebase the current branch?" yn
    case $yn in
        [Yy]* ) echo 'Rebasing ...';
        	git rebase $cur_dev $cur_branch;
        	cp site.properties.mysql site.properties
        	exit;;
        [Nn]* ) git checkout $cur_branch;
        	exit;;
        * ) echo "Please answer yes or no.";;
    esac
	done
else
	echo "Failed to checkout develop!" 1>&2
	exit 1
fi
