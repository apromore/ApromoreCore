# -*- coding: utf-8 -*-
from extraction import parameter_extraction as par
from support_modules.readers import readers as rd
from support_modules.writers import xml_writer as xml_bimp
from sys import argv

log_file_name = argv[1]
bpmn_file_name = argv[2]
output_file_bimp = argv[3]

print("-- Mining Simulation Parameters --")
timeformat = "%Y-%m-%dT%H:%M:%S"
log_columns_numbers = "[0]"
log, bpmn = rd.read_inputs(timeformat, log_columns_numbers, log_file_name, bpmn_file_name, False)
print("-- Successfully read log and model --")
parameters,_ = par.extract_parameters(log, bpmn)
xml_bimp.print_parameters(bpmn_file_name, output_file_bimp, parameters)
