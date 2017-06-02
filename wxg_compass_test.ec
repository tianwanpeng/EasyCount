[system]
sql=\
with (\
select "10013" logid, TimeStamp_ from log_10013 union all \
select "10025" logid, TimeStamp_ from log_10025 union all \
select "10059" logid, TimeStamp_ from log_10059 union all \
select "10348" logid, TimeStamp_ from log_10348 union all \
select "10377" logid, TimeStamp_ from log_10377 union all \
select "10380" logid, TimeStamp_ from log_10380 \
) uniontbl \
(select logid, TimeStamp_, unix_timestamp() from uniontbl where unix_timestamp()-TimeStamp_>60) tbl \
insert into __printtable select "0", logid, TimeStamp_, unix_timestamp() from tbl \
insert into __printtable select "1", logid, count(1) from uniontbl group by logid coordinate by unix_timestamp() with aggr interval 60 seconds


[tabledesc-log-10013]
table.name=log_10013
table.binary.mode=false
table.fields=LogID_,BIGINT,:ProtocolVersion_,BIGINT,:Uin_,BIGINT,:IfSuccess_,BIGINT,:Device_,BIGINT,:ClientVersion_,BIGINT,:ClientIP_,BIGINT,:AgentIP_,BIGINT,:TimeStamp_,BIGINT,:ExpandCol1_,BIGINT,:ExpandCol2_,BIGINT,:MsgType_,BIGINT,:FromUin_,BIGINT,
table.field.splitter=,
table.list.splitter=;
table.map.splitter=:
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10013


[tabledesc-log-10025]
table.name=log_10025
table.binary.mode=false
table.fields=LogID_,BIGINT,:ProtocolVersion_,BIGINT,:Uin_,BIGINT,:IfSuccess_,BIGINT,:Device_,BIGINT,:ClientVersion_,BIGINT,:ClientIP_,BIGINT,:AgentIP_,BIGINT,:TimeStamp_,BIGINT,:ExpandCol1_,BIGINT,:ExpandCol2_,BIGINT,:MsgType_,BIGINT,:ToUin_,BIGINT,:IsMassSend_,BIGINT,:MsgID_,BIGINT,:AppMsgID_,BIGINT,:Scene_,BIGINT,:AppId_,STRING,:MsgContent_,STRING,
table.field.splitter=,
table.list.splitter=;
table.map.splitter=:
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10025


[tabledesc-log-10059]
table.name=log_10059
table.binary.mode=false
table.fields=LogID_,BIGINT,:ProtocolVersion_,BIGINT,:Uin_,BIGINT,:IfSuccess_,BIGINT,:Device_,BIGINT,:ClientVersion_,BIGINT,:ClientIP_,BIGINT,:AgentIP_,BIGINT,:TimeStamp_,BIGINT,:ExpandCol1_,BIGINT,:ExpandCol2_,BIGINT,:Value_,BIGINT,:FriendUin_,BIGINT,:Scene_,BIGINT,:QrScene_,BIGINT,
table.field.splitter=,
table.list.splitter=;
table.map.splitter=:
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10059


[tabledesc-log-10348]
table.name=log_10348
table.binary.mode=false
table.fields=LogID_,BIGINT,:ProtocolVersion_,BIGINT,:Uin_,BIGINT,:IfSuccess_,BIGINT,:Device_,BIGINT,:ClientVersion_,BIGINT,:ClientIP_,BIGINT,:AgentIP_,BIGINT,:TimeStamp_,BIGINT,:ExpandCol1_,BIGINT,:ExpandCol2_,BIGINT,:UseCallBackApi_,BIGINT,:CallBackUrl_,STRING,:CallBackHost_,STRING,:CalkBackTimeCost_,BIGINT,:VerifyToken_,BIGINT,:CallBackHostIP_,STRING,:CallBackHostIPInt_,BIGINT,
table.field.splitter=,
table.list.splitter=;
table.map.splitter=:
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10348


[tabledesc-log-10377]
table.name=log_10377
table.binary.mode=false
table.fields=LogID_,BIGINT,:ProtocolVersion_,BIGINT,:Uin_,BIGINT,:IfSuccess_,BIGINT,:Device_,BIGINT,:ClientVersion_,BIGINT,:ClientIP_,BIGINT,:AgentIP_,BIGINT,:TimeStamp_,BIGINT,:ExpandCol1_,BIGINT,:ExpandCol2_,BIGINT,:bizuin_,BIGINT,:msgid_,BIGINT,:itemidx_,BIGINT,:scene_,BIGINT,:title_,STRING,:platform_,BIGINT,
table.field.splitter=,
table.list.splitter=;
table.map.splitter=:
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10377


[tabledesc-log-10380]
table.name=log_10380
table.binary.mode=false
table.fields=LogID_,BIGINT,:ProtocolVersion_,BIGINT,:Uin_,BIGINT,:IfSuccess_,BIGINT,:Device_,BIGINT,:ClientVersion_,BIGINT,:ClientIP_,BIGINT,:AgentIP_,BIGINT,:TimeStamp_,BIGINT,:ExpandCol1_,BIGINT,:ExpandCol2_,BIGINT,:bizuin_,BIGINT,:url_,STRING,:targeturl_,BIGINT,:MsgID_,BIGINT,:ItemIdx_,BIGINT,:ActionType_,BIGINT,:SrcUrl_,STRING,
table.field.splitter=,
table.list.splitter=;
table.map.splitter=:
table.type=tube
table.tube.master=tl-weixin-tube-master
table.tube.port=8609
table.topic=wxg_weixin_msg_oauth
table.interfaceId=10380





[tabledesc-res-r_fans]
table.name=r_fans
table.type=mysql
table.fields=time,string,:addfans,bigint,:minusfans,bigint,
table.mysql.db.host=10.156.8.48
table.mysql.db.port=3306
table.mysql.db.name=compass_checker
table.mysql.db.username=qspace
table.mysql.db.passwd=forconnect

[tabledesc-res-r_massmsgsend]
table.name=r_massmsgsend
table.type=mysql
table.fields=time,string,:msgnum,bigint,
table.mysql.db.host=10.156.8.48
table.mysql.db.port=3306
table.mysql.db.name=compass_checker
table.mysql.db.username=qspace
table.mysql.db.passwd=forconnect

[tabledesc-res-r_msgread]
table.name=r_msgread
table.type=mysql
table.fields=time,string,:msgread,bigint,
table.mysql.db.host=10.156.8.48
table.mysql.db.port=3306
table.mysql.db.name=compass_checker
table.mysql.db.username=qspace
table.mysql.db.passwd=forconnect

[tabledesc-res-r_orimsgsend]
table.name=r_orimsgsend
table.type=mysql
table.fields=time,string,:msgread,bigint,:msgshare,bigint,
table.mysql.db.host=10.156.8.48
table.mysql.db.port=3306
table.mysql.db.name=compass_checker
table.mysql.db.username=qspace
table.mysql.db.passwd=forconnect

[tabledesc-res-r_msgsend]
table.name=r_msgsend
table.type=mysql
table.fields=time,string,:msgsend,bigint,
table.mysql.db.host=10.156.8.48
table.mysql.db.port=3306
table.mysql.db.name=compass_checker
table.mysql.db.username=qspace
table.mysql.db.passwd=forconnect

[tabledesc-res-r_callback]
table.name=r_callback
table.type=mysql
table.fields=time,string,:UseCallBackApi,bigint,:callbacknum,bigint,
table.mysql.db.host=10.156.8.48
table.mysql.db.port=3306
table.mysql.db.name=compass_checker
table.mysql.db.username=qspace
table.mysql.db.passwd=forconnect

            
            
            
            
            
            
            