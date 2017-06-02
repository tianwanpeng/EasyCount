
[system]

sql=WITH \
(SELECT AGGRTIME agtime, url_md5, count(url_md5) url_cnt FROM yunhoutai_dsl_125_fht0 where step=2 and security=0 and OldSecurity!=6 GROUP BY url_md5 COORDINATE BY ts*1000 WITH AGGR INTERVAL 3600 SECONDS) tmp_tbl_url_cnt, \
(SELECT AGGRTIME agtime, domain_md5, count(domain_md5) domain_cnt FROM yunhoutai_dsl_125_fht0 where step=2 and newsecurity=0 and OldSecurity!=6  GROUP BY domain_md5 COORDINATE BY ts*1000 WITH AGGR INTERVAL 3600 SECONDS) tmp_tbl_domain_cnt \
\
INSERT INTO url_security_query_cnt SELECT from_unixtime(agtime, "yyyyMMddHH"), utb.md5, utb.cnt, utb.flag FROM (SELECT agtime, url_md5 md5, url_cnt cnt, 1 flag from tmp_tbl_url_cnt \
UNION ALL SELECT agtime, domain_md5 md5, domain_cnt cnt, 2 flag FROM tmp_tbl_domain_cnt) utb

[tabledesc-1]
table.name=yunhoutai_dsl_125_fht0
table.fields=ts,BIGINT,:ver,BIGINT,:url,STRING,URL:domain,STRING,:guid,STRING,Guid:uin,BIGINT,uin:SoftVer,BIGINT,SoftVer:ScanId,BIGINT,ScanId:OldSecurity,BIGINT,OldSecurity:NewSecurity,BIGINT,NewSecurity:HitRecordId,BIGINT,HitRecordId:Security,BIGINT,Security:url_md5,STRING,url_md5:domain_md5,STRING,domain_md5:step,bigInt,step
table.type=tube
table.tube.master=tl-normal-tube-master
table.tube.port=8609
table.topic=mig_pcmgrsafe_yunhoutai
table.interfaceId=125
table.field.splitter=;
splitter=;

[tabledesc-2]
table.name=url_security_query_cnt
table.fields=fdate,string,:fmd5,string,:fcnt,bigint,:fflag,bigint,
table.type=mysql
table.tpg.db.host=10.204.28.101
table.tpg.db.port=3466
table.tpg.db.name=url_security_query
table.tpg.db.username=com_pcgj
table.tpg.db.passwd=com@2014XbaYemorGfdwj

table.field.splitter=;
splitter=;

