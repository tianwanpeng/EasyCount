[system]
sql=with \
(select str_to_map(mon_msg, "&", "=") mapdata, ATTRS attr from trc_mon) mapTbl, \
(select \
map_get(mapdata, "consumerGroup") cg, \
map_get(mapdata, "interfaceId") iid, \
cast (map_get(mapdata, "monType") as int) mon_type, \
cast (map_get(mapdata, "cnt") as bigint) cnt, \
map_get(attr, "dt") dtime from mapTbl) parseTbl \
insert into __printtable select from_unixtime(AGGRTIME DIV 1000, "yyyyMMddHHmm") agtime, \
cg, if(iid=null, "null", iid), mon_type, sum(cnt) from parseTbl \
group by cg, if(iid=null, "null", iid), mon_type coordinate by dtime with aggr interval 60 seconds


[tabledesc-1-trc_mon]
table.name=trc_mon
table.fields=mon_msg,string,
table.topic=teg_trc_monitor
table.interfaceId=trc_mon
table.type=tube
table.tube.master=tl-vip-tube-master
table.tube.port=8609
table.field.splitter=0x0
splitter=0x0


