#!/bin/bash

dve2fiacre=build/install/dve2fiacre/bin/dve2fiacre

benchPath=/beem-benchmark/original-benchmark
newPath=/beem-benchmark/fiacre-gen
dveFiles=($(find ../$benchPath -iname "*.dve"))

echo "" > fiacre_generation.log

for dveFile in ${dveFiles[@]}; do
    if [[ $dveFile == *prop* ]]; then
        continue
    fi
    if [ -e $dveFile ]; then
        dveFile=($(realpath $dveFile))
        fcrFile=($(echo $dveFile |sed "s|${benchPath}|${newPath}|g"))
        fcrFile=($(echo $fcrFile |sed "s|\.dve|\.fcr|g"))
        fcrFile=($(echo $fcrFile |sed "s|\.|_|g"))
        fcrFile=($(echo $fcrFile |sed "s|_fcr|\.fcr|g"))

        mkdir -p -- "$(dirname -- "$fcrFile")"

        echo $(basename "$dveFile") "-->" $(basename "$fcrFile")
        $dve2fiacre -o $fcrFile $dveFile >> fiacre_generation.log 2>&1
    fi
done

trap ctrl_c INT

function ctrl_c() {
        echo "** Trapped CTRL-C"
}
