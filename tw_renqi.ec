[tabledesc-1-Favor]
table.name = qqread_qqbook_dsl_Favor_fdt0
table.fields = act_time,STRING,:qq_no,STRING,:bookid,BIGINT,:platform,STRING
table.type = tube
table.tube.master = tl-vip-tube-master
table.tube.port = 8609
table.topic = ieg_qqread_qqbook
table.interfaceId = Favor
table.field.splitter = |
spliter = |

[tabledesc-2-TjTicket]
table.name = qqread_qqbook_dsl_TjTicket_fdt0
table.fields = act_time,STRING,:data_type,STRING,:ticket_num,BIGINT,:bookid,BIGINT,:platform,STRING
table.type = tube
table.tube.master = tl-vip-tube-master
table.tube.port = 8609
table.topic = ieg_qqread_qqbook
table.interfaceId = TjTicket
table.field.splitter = |
spliter = |

[tabledesc-3-Fans]
table.name = qqread_qqbook_dsl_Fans_fdt0
table.fields = act_time,STRING,:data_type,BIGINT,:qq_no,STRING,:bookid,BIGINT,:prop_number,BIGINT,:add_fans_num,BIGINT,:current_lfas_num,BIGINT,:fans_level,BIGINT,:channel,BIGINT
table.type = tube
table.tube.master = tl-vip-tube-master
table.tube.port = 8609
table.topic = ieg_qqread_qqbook
table.interfaceId = Fans
table.field.splitter = |
spliter = |

[tabledesc-4-UserRead]
table.name = qqread_qqbook_dsl_UserRead_fdt0
table.fields = read_time,STRING,:platform,STRING,:qq_no,STRING,:bookid,BIGINT
table.type = tube
table.tube.master = tl-vip-tube-master
table.tube.port = 8609
table.topic = ieg_qqread_qqbook
table.interfaceId = UserRead
table.field.splitter = |
spliter = |

[tabledesc-dimtable-tde]
table.type = tde
table.name = bookrenqi
table.fields = k,string,:uvball10,binary,:totaluv10,bigint,:uvball15,binary,:totaluv15,bigint,:uvball20,binary,:totaluv20,bigint,:addfavor,bigint,:delfavor,bigint,:ticket,bigint,:fans,bigint
table.field.key = k
table.binary.mode = true
table.tde.read.async = true
table.tde.write.async = true
table.tde.master = 10.208.146.154:5198
table.tde.slave = 10.208.146.172:5198
table.tde.groupname = comm_gk_tdengine
table.tde.tid = 1006
table.tde.timeout = 5000

[tabledesc-dimtable2-tde]
table.type = tde
table.name = qqreaDuserinfo
table.fields = qq_no,string,:user_level,bigint,:vip_level,bigint,:is_baoyue,bigint,:is_black,bigint
table.field.key = qq_no
table.binary.mode = false
table.tde.read.async = true
table.tde.write.async = true
table.tde.master = 10.208.146.154:5198
table.tde.slave = 10.208.146.172:5198
table.tde.groupname = comm_gk_tdengine
table.tde.tid = 1016
table.tde.timeout = 5000

[tabledesc-2-res2-t_rst_qqbook_renqi_week]
table.name = t_rst_qqbook_renqi_week
table.fields = statTime,string,:platform,string,:bookid,bigint,:uv,bigint,:totaluv,bigint,:uv15,bigint,:totaluv15,bigint,:uv20,bigint,:totaluv20,bigint,:favor,bigint,:tjticket,bigint,:fans,bigint,:renqi,bigint,:ts,string
table.type = mysql
table.mysql.db.host = 10.194.1.101
table.mysql.db.port = 3306
table.mysql.db.name = sortlist
table.mysql.db.username = bangdan
table.mysql.db.passwd = bangdanDB2014

[tabledesc-3-res3-t_rst_qqbook_renqi_month]
table.name = t_rst_qqbook_renqi_month
table.fields = statTime,string,:platform,string,:bookid,bigint,:uv,bigint,:totaluv,bigint,:uv15,bigint,:totaluv15,bigint,:uv20,bigint,:totaluv20,bigint,:favor,bigint,:tjticket,bigint,:fans,bigint,:renqi,bigint,:ts,string
table.type = mysql
table.mysql.db.host = 10.194.1.101
table.mysql.db.port = 3306
table.mysql.db.name = sortlist
table.mysql.db.username = bangdan
table.mysql.db.passwd = bangdanDB2014

[system]
sql =  WITH (select qq_no,bookid,1L readpv,0L addfavor,0L delfavor, 0L ticket_num,0L fans ,case when (platform = 'wap' or platform = 'ubook') then 'page' else 'app' end platform ,regexp_extract(read_time, '^([0-9]{4}-[0-9]{2}-[0-9]{2}\\\\s[0-9]{2}:[0-9]{2}:[0-9]{2})$',1) dtEventTime from  qqread_qqbook_dsl_UserRead_fdt0 where qq_no>=10000  )ReadTblTmp,  (select ReadTblTmp.qq_no,bookid,readpv, addfavor,delfavor,ticket_num,fans,platform,dtEventTime from ReadTblTmp left join qqreaDuserinfo on ReadTblTmp.qq_no=qqreaDuserinfo.qq_no )ReadTbl,  (select qq_no,bookid,readpv, addfavor,delfavor,ticket_num,fans,platform,dtEventTime from( select qq_no,bookid,0L readpv,1L addfavor,0L delfavor,0L ticket_num,0L fans,'page' platform ,regexp_extract(act_time, '^([0-9]{4}-[0-9]{2}-[0-9]{2}\\\\s[0-9]{2}:[0-9]{2}:[0-9]{2})$',1) dtEventTime from qqread_qqbook_dsl_Favor_fdt0 where regexp_extract(act_time, '^(.*)(add)(.*)$',2)='add' union all select qq_no,bookid,0L readpv,0L addfavor,1L delfavor,0L ticket_num,0L fans,'page' platform ,regexp_extract(act_time, '^([0-9]{4}-[0-9]{2}-[0-9]{2}\\\\s[0-9]{2}:[0-9]{2}:[0-9]{2})$',1) dtEventTime from qqread_qqbook_dsl_Favor_fdt0 where regexp_extract(act_time, '^(.*)(add)(.*)$',2)='remove' union all select qq_no,bookid,0L readpv,1L addfavor,0L delfavor,0L ticket_num,0L fans,'app' platform ,regexp_extract(act_time, '^([0-9]{4}-[0-9]{2}-[0-9]{2}\\\\s[0-9]{2}:[0-9]{2}:[0-9]{2})$',1) dtEventTime from qqread_qqbook_dsl_Favor_fdt0 where regexp_extract(act_time, '^(.*)(add)(.*)$',2)='add' union all select qq_no,bookid,0L readpv,0L addfavor,1L delfavor,0L ticket_num,0L fans,'app' platform ,regexp_extract(act_time, '^([0-9]{4}-[0-9]{2}-[0-9]{2}\\\\s[0-9]{2}:[0-9]{2}:[0-9]{2})$',1) dtEventTime from qqread_qqbook_dsl_Favor_fdt0 where regexp_extract(act_time, '^(.*)(add)(.*)$',2)='remove' union all select regexp_extract(act_time, '^(.*)(INFO)(\\s+)(\\d+)$',4)  qq_no,bookid,0L readpv,0L addfavor,0L delfavor, ticket_num,0L fans,'page' platform ,regexp_extract(act_time, '^([0-9]{4}-[0-9]{2}-[0-9]{2}\\\\s[0-9]{2}:[0-9]{2}:[0-9]{2})$',1) dtEventTime from qqread_qqbook_dsl_TjTicket_fdt0 union all select regexp_extract(act_time, '^(.*)(INFO)(\\s+)(\\d+)$',4)  qq_no,bookid,0L readpv,0L addfavor,0L delfavor, ticket_num,0L fans,'app' platform ,regexp_extract(act_time, '^([0-9]{4}-[0-9]{2}-[0-9]{2}\\\\s[0-9]{2}:[0-9]{2}:[0-9]{2})$',1) dtEventTime from qqread_qqbook_dsl_TjTicket_fdt0 union all select qq_no,bookid,0L readpv,0L addfavor,0L delfavor, 0L ticket_num,add_fans_num fans,'page' platform ,regexp_extract(act_time, '^([0-9]{4}-[0-9]{2}-[0-9]{2}\\\\s[0-9]{2}:[0-9]{2}:[0-9]{2})$',1) dtEventTime from qqread_qqbook_dsl_Fans_fdt0 union all select qq_no,bookid,0L readpv,0L addfavor,0L delfavor, 0L ticket_num,add_fans_num fans,'app' platform ,regexp_extract(act_time, '^([0-9]{4}-[0-9]{2}-[0-9]{2}\\\\s[0-9]{2}:[0-9]{2}:[0-9]{2})$',1) dtEventTime from qqread_qqbook_dsl_Fans_fdt0 )t ) otherTbl,  ( select qq_no,bookid,readpv, addfavor,delfavor,ticket_num,fans,platform,dtEventTime,user_level,vip_level,is_baoyue from ( select qq_no,bookid,readpv, addfavor,delfavor,ticket_num,fans,platform,dtEventTime,0L user_level,1L vip_level,0L is_baoyue  from ReadTbl union all select qq_no,bookid,readpv, addfavor,delfavor,ticket_num,fans,platform,dtEventTime,0L user_level,0L vip_level,0L is_baoyue from otherTbl )parseTbltmp where bookid>0 )parseTbl,  (SELECT platform, bookid, concat(date_add('2014-06-08', (weekofyear(from_unixtime((AGGRTIME DIV 1000), 'yyyy-MM-dd'))-24)*7+1),' 00:00:00') timeW, from_unixtime((AGGRTIME DIV 1000), 'yyyy-MM-00 00:00:00') timeM, concat_ws('-', 'w', concat(date_add('2014-06-08' ,(weekofyear(from_unixtime((AGGRTIME DIV 1000), 'yyyy-MM-dd'))-24)*7+1),' 00:00:00'),platform,cast (bookid as string)) kw, concat_ws('-', 'm', from_unixtime((AGGRTIME DIV 1000), 'yyyy-MM-00 00:00:00'), platform, cast (bookid as string)) km, hllp(case when readpv>0 and vip_level=0 then qq_no end) uvb10, hllp(case when readpv>0 and vip_level>=1 and vip_level<=3 then qq_no end) uvb15, hllp(case when readpv>0 and vip_level>=4   then qq_no end) uvb20, sum(addfavor) addfavor,sum(delfavor) delfavor,sum(ticket_num) ticket, sum(fans) fans, from_unixtime(unix_timestamp()) ts FROM parseTbl GROUP BY platform,bookid COORDINATE BY unix_timestamp(dtEventTime, 'yyyy-MM-dd HH:mm:ss')*1000 WITH AGGR INTERVAL 900 SECONDS) aggrTbl,   (SELECT aggrTbl.kw, timeW statTime, aggrTbl.platform, aggrTbl.bookid, case when  from_unixtime(unix_timestamp(aggrTbl.ts),'HHmm')<'0005' then cast('' as binary) else hllp_merge(aggrTbl.uvb10, bookrenqi.uvball10)  end uvball10, case when  from_unixtime(unix_timestamp(aggrTbl.ts),'HHmm')<'0005'  then  hllp_get(hllp_merge(aggrTbl.uvb10, bookrenqi.uvball10)) +nvl(bookrenqi.totaluv10, 0L) else nvl(bookrenqi.totaluv10, 0L) end totaluv10, case when  from_unixtime(unix_timestamp(aggrTbl.ts),'HHmm')<'0005' then cast('' as binary) else hllp_merge(aggrTbl.uvb15, bookrenqi.uvball15)  end uvball15, case when  from_unixtime(unix_timestamp(aggrTbl.ts),'HHmm')<'0005'  then  hllp_get(hllp_merge(aggrTbl.uvb15, bookrenqi.uvball15)) +nvl(bookrenqi.totaluv15, 0L) else nvl(bookrenqi.totaluv15, 0L) end totaluv15, case when  from_unixtime(unix_timestamp(aggrTbl.ts),'HHmm')<'0005' then cast('' as binary) else hllp_merge(aggrTbl.uvb20, bookrenqi.uvball20)  end uvball20, case when  from_unixtime(unix_timestamp(aggrTbl.ts),'HHmm')<'0005'  then  hllp_get(hllp_merge(aggrTbl.uvb20, bookrenqi.uvball20)) +nvl(bookrenqi.totaluv20, 0L) else nvl(bookrenqi.totaluv20, 0L) end totaluv20, (aggrTbl.addfavor + nvl(bookrenqi.addfavor, 0L)) addfavor, (aggrTbl.delfavor + nvl(bookrenqi.delfavor, 0L)) delfavor, (aggrTbl.ticket + nvl(bookrenqi.ticket, 0L)) ticket, (aggrTbl.fans + nvl(bookrenqi.fans, 0L)) fans , aggrTbl.ts FROM aggrTbl left join bookrenqi on aggrTbl.kw=bookrenqi.k) jw,  (SELECT aggrTbl.km, timeM statTime, aggrTbl.platform, aggrTbl.bookid, case when  from_unixtime(unix_timestamp(aggrTbl.ts),'HHmm')<'0005' then cast('' as binary) else hllp_merge(aggrTbl.uvb10, bookrenqi.uvball10)  end uvball10, case when  from_unixtime(unix_timestamp(aggrTbl.ts),'HHmm')<'0005'  then  hllp_get(hllp_merge(aggrTbl.uvb10, bookrenqi.uvball10)) +nvl(bookrenqi.totaluv10, 0L) else nvl(bookrenqi.totaluv10, 0L) end totaluv10, case when  from_unixtime(unix_timestamp(aggrTbl.ts),'HHmm')<'0005' then cast('' as binary) else hllp_merge(aggrTbl.uvb15, bookrenqi.uvball15)  end uvball15, case when  from_unixtime(unix_timestamp(aggrTbl.ts),'HHmm')<'0005'  then  hllp_get(hllp_merge(aggrTbl.uvb15, bookrenqi.uvball15)) +nvl(bookrenqi.totaluv15, 0L) else nvl(bookrenqi.totaluv15, 0L) end totaluv15, case when  from_unixtime(unix_timestamp(aggrTbl.ts),'HHmm')<'0005' then cast('' as binary) else hllp_merge(aggrTbl.uvb20, bookrenqi.uvball20)  end uvball20, case when  from_unixtime(unix_timestamp(aggrTbl.ts),'HHmm')<'0005'  then  hllp_get(hllp_merge(aggrTbl.uvb20, bookrenqi.uvball20)) +nvl(bookrenqi.totaluv20, 0L) else nvl(bookrenqi.totaluv20, 0L) end totaluv20, (aggrTbl.addfavor + nvl(bookrenqi.addfavor, 0L)) addfavor, (aggrTbl.delfavor + nvl(bookrenqi.delfavor, 0L)) delfavor, (aggrTbl.ticket + nvl(bookrenqi.ticket, 0L)) ticket, (aggrTbl.fans + nvl(bookrenqi.fans, 0L)) fans , aggrTbl.ts FROM aggrTbl left join bookrenqi on aggrTbl.km=bookrenqi.k) jm,  (SELECT k, uvball10,totaluv10,uvball15,totaluv15,uvball20,totaluv20,addfavor,delfavor,ticket,fans FROM ( SELECT kw k, uvball10,totaluv10,uvball15,totaluv15,uvball20,totaluv20,addfavor,delfavor,ticket,fans FROM jw UNION ALL SELECT km k, uvball10,totaluv10,uvball15,totaluv15,uvball20,totaluv20,addfavor,delfavor,ticket,fans FROM jm ) jhd_utmp) jwm  INSERT INTO bookrenqi WITH k as KEY SELECT k, uvball10,totaluv10,uvball15,totaluv15,uvball20,totaluv20,addfavor,delfavor,ticket,fans FROM jwm, INSERT INTO t_rst_qqbook_renqi_week SELECT statTime, platform, bookid, nvl(hllp_get(uvball10),0L),totaluv10,nvl(hllp_get(uvball15),0L),totaluv15,nvl(hllp_get(uvball20),0L),totaluv20, (addfavor-delfavor) favor,ticket,fans, ceil(((nvl(hllp_get(uvball10),0L)+totaluv10)+(nvl(hllp_get(uvball15),0L)+totaluv15)*1.5+(nvl(hllp_get(uvball20),0L)+totaluv20)*2)/10+ (addfavor-delfavor)*10+ticket*5+fans/10) renqi,ts FROM jw ,  INSERT INTO t_rst_qqbook_renqi_month SELECT statTime, platform, bookid, nvl(hllp_get(uvball10),0L),totaluv10,nvl(hllp_get(uvball15),0L),totaluv15,nvl(hllp_get(uvball20),0L),totaluv20, ceil(((nvl(hllp_get(uvball10),0L)+totaluv10)+(nvl(hllp_get(uvball15),0L)+totaluv15)*1.5+(nvl(hllp_get(uvball20),0L)+totaluv20)*2)/10+ (addfavor-delfavor)*10+ticket*5+fans/10) renqi,ts FROM jm 

