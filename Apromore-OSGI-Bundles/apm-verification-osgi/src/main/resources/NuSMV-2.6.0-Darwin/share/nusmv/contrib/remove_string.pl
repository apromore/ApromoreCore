#!/usr/bin/perl

# Copyright (C) 2009 Marco Roveri.
# Originally written by Marco Roveri <roveri@fbk.eu>
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# General Public License for more details.

# This program reads an SMV file and prints on stdout an sh script
# invoking perl for removing all the string names occurring in the
# file, as to make it more readbale.
#
# Usage:
#  remove_strings.pl < file.smv > rep.sh
#  cp file.smv file_ns.smv
#  sh ./rep.sh file_ns.smv
#
# All the strings are replaced with a unique name of the form
# ___MMMM<num>___

my %str;

while(<STDIN>) {
    # $s = chomp;
    my $s = $_;

    if (/\"[^\"]+\"/) {
        my $p = $s;
        while ($p =~ s/(\"[^\"]+\")//) {
            my $k = $1;
            $k =~ s/\\/\\\\/g;
            $str{$k} = 1;
        }
    }
}

print "\#!/bin/sh\n\n";
my $k;
my $n = 0;
foreach $k (keys %str) {
    $k =~ s/\"/\\\"/g;
    my $st = "__MMMM${n}__";
    $n += 1;
    $k =~ s/\(/\\\(/g;
    $k =~ s/\)/\\\)/g;
    $k =~ s/\|/\\\|/g;
    $k =~ s/\$/\\\$/g;
    $k =~ s/\@/\\\@/g;
    $k =~ s/\#/\\\#/g;
    $k =~ s/\&/\\\&/g;
    print "perl -pi -e 's/$k/$st/g' \$1\n";
}
