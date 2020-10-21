CREATE TABLE `apromore`.`usermetadata_bak` select * from usermetadata;
ALTER TABLE `apromore`.`usermetadata`
ADD COLUMN `name` VARCHAR(255);
update (select id,substring(content,10,instr(content,"\",\"criteria")-10) as name FROM `usermetadata`) b,usermetadata
a set a.name = b.name WHERE a.id = b.id;