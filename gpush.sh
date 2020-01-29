#!/bin/sh
# Force push current feature branch
#
# ./gpush.sh remote-branch-name remote-name
#
cur_branch=$(git rev-parse --abbrev-ref HEAD)
if [ -z "$1" ]
    then
        remote_branch=$cur_branch
    else
        remote_branch=$1
fi
if [ -z "$2" ]
    then
        cur_remote=$(git remote | grep origin | tail -n 1)
        if [ -z "$cur_remote" ]
          then
            cur_remote=$(git remote | tail -n 1)
        fi
    else
        cur_remote=$2
fi
if [ -z "$cur_remote" ]
    then
	    echo "Failed to get remote!" 1>&2
	exit 1
fi
git push $cur_remote $cur_branch:$remote_branch --force
