                [system]
sql=\
insert into res_tube_10073 with attrs as attrs select msgdata from log_10073, \ 
insert into res_tube_10074 with attrs as attrs select msgdata from log_10074, \ 
insert into res_tube_10075 with attrs as attrs select msgdata from log_10075, \ 
insert into res_tube_10420 with attrs as attrs select msgdata from log_10420, \ 
insert into res_tube_10421 with attrs as attrs select msgdata from log_10421, \ 
insert into res_tube_10736 with attrs as attrs select msgdata from log_10736, \ 
insert into res_tube_10410 with attrs as attrs select msgdata from log_10410, \ 
insert into res_tube_10612 with attrs as attrs select msgdata from log_10612, \
insert into res_tube_10069 with attrs as attrs select msgdata from log_10069, \
\
insert into res_10748 with attrs as attrs select msgdata from log_10748, \
\
insert into res_10527 with attrs as attrs select msgdata from log_10527, \
insert into res_10528 with attrs as attrs select msgdata from log_10528, \
insert into res_10978 with attrs as attrs select msgdata from log_10978, \
insert into res_10013 with attrs as attrs select msgdata from log_10013, \ 
insert into res_10026 with attrs as attrs select msgdata from log_10026, \ 
insert into res_10059 with attrs as attrs select msgdata from log_10059, \ 
insert into res_10348 with attrs as attrs select msgdata from log_10348, \ 
insert into res_10377 with attrs as attrs select msgdata from log_10377, \ 
insert into res_10380 with attrs as attrs select msgdata from log_10380 \
\
\
with (\
select "10073" id from log_10073 union all \
select "10074" id from log_10074 union all \
select "10075" id from log_10075 union all \
select "10420" id from log_10420 union all \
select "10421" id from log_10421 union all \
select "10736" id from log_10736 union all \
select "10410" id from log_10410 union all \
select "10612" id from log_10612 union all \
select "10748" id from log_10748 union all \
select "10527" id from log_10527 union all \
select "10528" id from log_10528 union all \
select "10978" id from log_10978 union all \
select "10013" id from log_10013 union all \
select "10026" id from log_10026 union all \
select "10059" id from log_10059 union all \
select "10348" id from log_10348 union all \
select "10377" id from log_10377 union all \
select "10380" id from log_10380 union all \
select "10069" id from log_10069) moniterinfo \
insert into t_trc_distribute_wx_moniter_info select from_unixtime(AGGRTIME DIV 1000, "yyyyMMddHHmm"), id, count(1), \
from_unixtime(unix_timestamp(), "yyyy-MM-dd HH:mm:ss") \
from moniterinfo group by id coordinate by unix_timestamp()*1000 with aggr interval 60 seconds


[tabledesc-10073]
table.name=log_10073
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10073
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10074]
table.name=log_10074
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10074
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10075]
table.name=log_10075
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10075
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10420]
table.name=log_10420
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10420
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10421]
table.name=log_10421
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10421
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10736]
table.name=log_10736
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10736
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10410]
table.name=log_10410
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10410
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10612]
table.name=log_10612
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10612
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10527]
table.name=log_10527
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10527
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10528]
table.name=log_10528
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10528
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10978]
table.name=log_10978
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10978
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10748]
table.name=log_10748
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10748
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10013]
table.name=log_10013
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10013
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10026]
table.name=log_10026
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10026
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10059]
table.name=log_10059
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10059
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10348]
table.name=log_10348
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10348
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10377]
table.name=log_10377
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10377
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10380]
table.name=log_10380
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10380
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10133]
table.name=log_10133
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10133
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10393]
table.name=log_10393
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10393
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10392]
table.name=log_10392
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10392
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

[tabledesc-10069]
table.name=log_10069
table.fields=msgdata,string,
table.field.splitter=0x0
table.topic=cdg_weixin_perf_msg
table.interfaceId=10069
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609

























[tabledesc-res-10748]
table.name=res_10748
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.zk.servers=sk-zk-td1:2181,sk-zk-td2:2181,sk-zk-td3:2181,sk-zk-td4:2181,sk-zk-td5:2181
table.zk.root=/meta_gdt
table.topic=sng_weixin_msg_to_gdt
table.interfaceId=10748


[tabledesc-res-10527]
table.name=res_10527
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10527

[tabledesc-res-10528]
table.name=res_10528
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10528

[tabledesc-res-10978]
table.name=res_10978
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10978

[tabledesc-res-10013]
table.name=res_10013
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10013

[tabledesc-res-10026]
table.name=res_10026
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10026

[tabledesc-res-10059]
table.name=res_10059
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10059

[tabledesc-res-10348]
table.name=res_10348
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10348

[tabledesc-res-10377]
table.name=res_10377
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10377

[tabledesc-res-10380]
table.name=res_10380
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10380

[tabledesc-res-tube-10073]
table.name=res_tube_10073
table.type=tube
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.tube.master=10.215.132.176
table.tube.port=8609
table.topic=teg_weixin_msg_to_jp
table.interfaceId=10073

[tabledesc-res-tube-10074]
table.name=res_tube_10074
table.type=tube
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.tube.master=10.215.132.176
table.tube.port=8609
table.topic=teg_weixin_msg_to_jp
table.interfaceId=10074

[tabledesc-res-tube-10075]
table.name=res_tube_10075
table.type=tube
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.tube.master=10.215.132.176
table.tube.port=8609
table.topic=teg_weixin_msg_to_jp
table.interfaceId=10075

[tabledesc-res-tube-10420]
table.name=res_tube_10420
table.type=tube
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.tube.master=10.215.132.176
table.tube.port=8609
table.topic=teg_weixin_msg_to_jp
table.interfaceId=10420

[tabledesc-res-tube-10421]
table.name=res_tube_10421
table.type=tube
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.tube.master=10.215.132.176
table.tube.port=8609
table.topic=teg_weixin_msg_to_jp
table.interfaceId=10421

[tabledesc-res-tube-10736]
table.name=res_tube_10736
table.type=tube
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.tube.master=10.215.132.176
table.tube.port=8609
table.topic=teg_weixin_msg_to_jp
table.interfaceId=10736

[tabledesc-res-tube-10410]
table.name=res_tube_10410
table.type=tube
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.tube.master=10.215.132.176
table.tube.port=8609
table.topic=teg_weixin_msg_to_jp
table.interfaceId=10410

[tabledesc-res-tube-10612]
table.name=res_tube_10612
table.type=tube
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.tube.master=10.215.132.176
table.tube.port=8609
table.topic=teg_weixin_msg_to_jp
table.interfaceId=10612



[tabledesc-moniter_info]
table.name=t_trc_distribute_wx_moniter_info
table.type=mysql
table.fields=logtime,string,:logid,string,:cnt,int,:updatetime,string,
table.mysql.db.host=10.185.20.109
table.mysql.db.port=3307
table.mysql.db.name=tdbank_config
table.mysql.db.username=tdbank
table.mysql.db.passwd=td_ht@2013

[tabledesc-res-tube-10069]
table.name=res_tube_10069
table.type=tube
table.binary.mode=false
table.fields=msgdata,string,
table.field.splitter=0x0
table.tube.master = tl-vip-tube-master
table.tube.port=8609
table.topic=cdg_weixin_to_cdg
table.interfaceId=10069




            