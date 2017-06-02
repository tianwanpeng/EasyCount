                [tabledesc-7-PlayerRegister]
table.name = wefeng_dsl_playerregister_fht0
table.fields = worldid,string,:__tablename,string,:vgamesvrid,string,:dteventtime,string,:isequence,bigint,:vgameappid,string,:vopenid,string,:platid,bigint,:clientversion,string,:systemsoftware,string,:systemhardware,string,:telecomoper,string,:network,string,:screenwidth,bigint,:screenhight,bigint,:density,double,:channel,bigint,:uuid,string,:cpuhardware,string,:memory,bigint,:glrender,string,:glversion,string,:deviceid,string,:vip,bigint,:viplv,bigint,
table.topic = hy_wefeng
table.interfaceId = PlayerRegister
table.type = tube
table.tube.master = tl-vip-tube-master
table.tube.port = 8609
table.field.splitter = |
splitter = |

[tabledesc-7-PlayerLogin]
table.name = wefeng_dsl_playerlogin_fht0
table.fields = worldid,string,:__tablename,string,:vgamesvrid,string,:dteventtime,string,:isequence,bigint,:vgameappid,string,:vopenid,string,:ilevel,bigint,:platid,bigint,:clientversion,string,:systemsoftware,string,:systemhardware,string,:telecomoper,string,:network,string,:screenwidth,bigint,:screenhight,bigint,:density,double,:channel,bigint,:uuid,string,:cpuhardware,string,:memory,bigint,:glrender,string,:glversion,string,:deviceid,string,:vip,bigint,:viplv,bigint,:connecttype,bigint,
table.topic = hy_wefeng
table.interfaceId = PlayerLogin
table.type = tube
table.tube.master = tl-vip-tube-master
table.tube.port = 8609
table.field.splitter = |
splitter = |

[tabledesc-7-PlayerLogout]
table.name = wefeng_dsl_playerlogout_fht0
table.fields = worldid,string,:__tablename,string,:vgamesvrid,string,:dteventtime,string,:isequence,bigint,:vgameappid,string,:vopenid,string,:ionlinetime,bigint,:ilevel,bigint,:platid,bigint,:clientversion,string,:systemsoftware,string,:systemhardware,string,:telecomoper,string,:network,string,:screenwidth,bigint,:screenhight,bigint,:density,double,:channel,bigint,:uuid,string,:cpuhardware,string,:memory,bigint,:glrender,string,:glversion,string,:deviceid,string,:vip,bigint,:viplv,bigint,:friendcount,double,
table.topic = hy_wefeng
table.interfaceId = PlayerLogout
table.type = tube
table.tube.master = tl-vip-tube-master
table.tube.port = 8609
table.field.splitter = |
splitter = |

[tabledesc-7-MoneyFlow]
table.name = wefeng_dsl_moneyflow_fht0
table.fields = worldid,string,:__tablename,string,:vgamesvrid,string,:dteventtime,string,:isequence,bigint,:vgameappid,string,:ieventid,string,:vopenid,string,:platid,bigint,:irolelevel,bigint,:ibeforemoney,bigint,:iaftermoney,bigint,:imoney,bigint,:iflowtype,bigint,:iaction,bigint,
table.topic = hy_wefeng
table.interfaceId = MoneyFlow
table.type = tube
table.tube.master = tl-vip-tube-master
table.tube.port = 8609
table.field.splitter = |
splitter = |

[tabledesc-7-AttrMoneyFlow]
table.name = wefeng_dsl_attrmoneyflow_fht0
table.fields = worldid,string,:__tablename,string,:vgamesvrid,string,:dteventtime,string,:isequence,bigint,:vgameappid,string,:vopenid,string,:platid,bigint,:result,bigint,:count,bigint,:money,bigint,:moneytype,bigint,:iflowtype,bigint,
table.topic = hy_wefeng
table.interfaceId = AttrMoneyFlow
table.type = tube
table.tube.master = tl-vip-tube-master
table.tube.port = 8609
table.field.splitter = |
splitter = |

[tabledesc-7-ItemMoneyFlow]
table.name = wefeng_dsl_itemmoneyflow_fht0
table.fields = worldid,string,:__tablename,string,:vgamesvrid,string,:dteventtime,string,:isequence,bigint,:vgameappid,string,:vopenid,string,:platid,bigint,:itemtype,bigint,:itemid,bigint,:count,bigint,:money,bigint,:moneytype,bigint,:reason,bigint,:level,bigint,:activityid,bigint,:activitydiscount,bigint,
table.topic = hy_wefeng
table.interfaceId = ItemMoneyFlow
table.type = tube
table.tube.master = tl-vip-tube-master
table.tube.port = 8609
table.field.splitter = |
splitter = |

[tabledesc-res-packagecreaterole]
table.name = rtc_idata_package_createrole_m
table.fields = vdatetime_m,string,:vgame,string,:ipackage,int,:iminutenum,bigint,
table.type = tpg
table.tpg.db.host = ieg-bi-tdw.tencent-distribute.com
table.tpg.db.port = 5432
table.tpg.db.name = ieg_idata
table.tpg.db.username = u_ieg_idata
table.tpg.db.passwd = u_ieg_idata@2014

[tabledesc-res-packagerolelogin]
table.name = rtc_idata_package_rolelogin_m
table.fields = vdatetime_m,string,:vgame,string,:ipackage,int,:iminutenum,bigint,
table.type = tpg
table.tpg.db.host = ieg-bi-tdw.tencent-distribute.com
table.tpg.db.port = 5432
table.tpg.db.name = ieg_idata
table.tpg.db.username = u_ieg_idata
table.tpg.db.passwd = u_ieg_idata@2014

[tabledesc-res-packagerolelogout]
table.name = rtc_idata_package_rolelogout_m
table.fields = vdatetime_m,string,:vgame,string,:ipackage,int,:iminutenum,bigint,
table.type = tpg
table.tpg.db.host = ieg-bi-tdw.tencent-distribute.com
table.tpg.db.port = 5432
table.tpg.db.name = ieg_idata
table.tpg.db.username = u_ieg_idata
table.tpg.db.passwd = u_ieg_idata@2014

[tabledesc-res-packagediamondin]
table.name = rtc_idata_package_diamond_in_m
table.fields = vdatetime_m,string,:vgame,string,:ipackage,int,:ireason,bigint,:iminutenum,bigint,
table.type = tpg
table.tpg.db.host = ieg-bi-tdw.tencent-distribute.com
table.tpg.db.port = 5432
table.tpg.db.name = ieg_idata
table.tpg.db.username = u_ieg_idata
table.tpg.db.passwd = u_ieg_idata@2014

[tabledesc-res-packagediamondout]
table.name = rtc_idata_package_diamond_out_m
table.fields = vdatetime_m,string,:vgame,string,:ipackage,int,:ireason,bigint,:iminutenum,bigint,
table.type = tpg
table.tpg.db.host = ieg-bi-tdw.tencent-distribute.com
table.tpg.db.port = 5432
table.tpg.db.name = ieg_idata
table.tpg.db.username = u_ieg_idata
table.tpg.db.passwd = u_ieg_idata@2014

[tabledesc-res-packagegoldcoinin]
table.name = rtc_idata_package_goldcoin_in_m
table.fields = vdatetime_m,string,:vgame,string,:ipackage,int,:ireason,bigint,:iminutenum,bigint,
table.type = tpg
table.tpg.db.host = ieg-bi-tdw.tencent-distribute.com
table.tpg.db.port = 5432
table.tpg.db.name = ieg_idata
table.tpg.db.username = u_ieg_idata
table.tpg.db.passwd = u_ieg_idata@2014

[tabledesc-res-packagegoldcoinout]
table.name = rtc_idata_package_goldcoin_out_m
table.fields = vdatetime_m,string,:vgame,string,:ipackage,int,:ireason,bigint,:iminutenum,bigint,
table.type = tpg
table.tpg.db.host = ieg-bi-tdw.tencent-distribute.com
table.tpg.db.port = 5432
table.tpg.db.name = ieg_idata
table.tpg.db.username = u_ieg_idata
table.tpg.db.passwd = u_ieg_idata@2014

[tabledesc-packstream]
table.name = packstream
table.type = tube
table.fields = openid,string,:vgame,string,:ibagid,int,:unixtime,bigint,
table.topic = ieg_idata
table.interfaceId = trace_user_bag
table.tube.master = tl-vip-tube-master
table.tube.port = 8609
table.field.splitter = |
splitter = |

[tabledesc-res-packdim]
table.name = packdim
table.fields = agtime,bigint,:hash_id,int,:pack_id,int,:bloom,binary,
table.type = tpg
table.tpg.db.host = ieg-bi-tdw.tencent-distribute.com
table.tpg.db.port = 5432
table.tpg.db.name = ieg_idata
table.tpg.db.username = u_ieg_idata
table.tpg.db.passwd = u_ieg_idata@2014
table.db.update.interval = 180
table.db.keyname = hash_id

[system]
sql =  with  (select "wefeng" bname, vOpenID openid, to_unix_timestamp(dtEventTime,"yyyy-MM-dd HH:mm:ss") agtime from wefeng_dsl_playerregister_fht0) wefeng_cr,  (select "wefeng" bname, vOpenID openid, iflowtype r, money m, to_unix_timestamp(dtEventTime,"yyyy-MM-dd HH:mm:ss") agtime from wefeng_dsl_attrmoneyflow_fht0 where moneytype=1) wefeng_diamond_in,  (select "wefeng" bname, vOpenID openid, reason r, money m, to_unix_timestamp(dtEventTime,"yyyy-MM-dd HH:mm:ss") agtime from wefeng_dsl_itemmoneyflow_fht0 where moneytype=1) wefeng_diamond_out,  (select "wefeng" bname, vOpenID openid, iAction r, iMoney m, to_unix_timestamp(dtEventTime,"yyyy-MM-dd HH:mm:ss") agtime from wefeng_dsl_moneyflow_fht0 where iFlowType=0) wefeng_goldcoin_in,  (select "wefeng" bname, vOpenID openid, iAction r, iMoney m, to_unix_timestamp(dtEventTime,"yyyy-MM-dd HH:mm:ss") agtime from wefeng_dsl_moneyflow_fht0 where iFlowType=1) wefeng_goldcoin_out,  (select "wefeng" bname, vOpenID openid, to_unix_timestamp(dtEventTime,"yyyy-MM-dd HH:mm:ss") agtime from wefeng_dsl_playerlogin_fht0) wefeng_ri,  (select "wefeng" bname, vOpenID openid, to_unix_timestamp(dtEventTime,"yyyy-MM-dd HH:mm:ss") agtime from wefeng_dsl_playerlogout_fht0) wefeng_ro    insert into rtc_idata_package_createrole_m select from_unixtime(AGGRTIME DIV 1000, 'yyyyMMddHHmm') vdatetime_m, bname vgame, packdim.pack_id ipackage, count(openid) from (select bname, openid, agtime from wefeng_cr ) utbl left join packdim on hash(concat(utbl.openid, "-", utbl.bname)) % 1000 = packdim.hash_id where bloom_contains(packdim.bloom, hash(concat(utbl.openid, "-", utbl.bname))) group by bname, packdim.pack_id coordinate by agtime*1000 with aggr interval 60 seconds   insert into rtc_idata_package_rolelogin_m select from_unixtime(AGGRTIME DIV 1000, 'yyyyMMddHHmm') vdatetime_m, bname vgame, packdim.pack_id ipackage, count(openid) from (select bname, openid, agtime from wefeng_ri ) utbl1 left join packdim on hash(concat(utbl1.openid, "-", utbl1.bname)) % 1000 = packdim.hash_id where bloom_contains(packdim.bloom, hash(concat(utbl1.openid, "-", utbl1.bname))) group by bname, packdim.pack_id coordinate by agtime*1000 with aggr interval 60 seconds   insert into rtc_idata_package_rolelogout_m select from_unixtime(AGGRTIME DIV 1000, 'yyyyMMddHHmm') vdatetime_m, bname vgame, packdim.pack_id ipackage, count(openid) from (select bname, openid, agtime from wefeng_ro ) utbl2 left join packdim on hash(concat(utbl2.openid, "-", utbl2.bname)) % 1000 = packdim.hash_id where bloom_contains(packdim.bloom, hash(concat(utbl2.openid, "-", utbl2.bname))) group by bname, packdim.pack_id coordinate by agtime*1000 with aggr interval 60 seconds   insert into rtc_idata_package_diamond_in_m select from_unixtime(AGGRTIME DIV 1000, 'yyyyMMddHHmm') vdatetime_m, bname vgame, packdim.pack_id ipackage, r ireason, sum(m) from (select bname, openid, r, m, agtime from wefeng_diamond_in ) utbl3 left join packdim on hash(concat(utbl3.openid, "-", utbl3.bname)) % 1000 = packdim.hash_id where bloom_contains(packdim.bloom, hash(concat(utbl3.openid, "-", utbl3.bname))) group by bname, packdim.pack_id, r coordinate by agtime*1000 with aggr interval 60 seconds   insert into rtc_idata_package_diamond_out_m select from_unixtime(AGGRTIME DIV 1000, 'yyyyMMddHHmm') vdatetime_m, bname vgame, packdim.pack_id ipackage, r ireason, sum(m) from (select bname, openid, r, m, agtime from wefeng_diamond_out ) utbl4 left join packdim on hash(concat(utbl4.openid, "-", utbl4.bname)) % 1000 = packdim.hash_id where bloom_contains(packdim.bloom, hash(concat(utbl4.openid, "-", utbl4.bname))) group by bname, packdim.pack_id, r coordinate by agtime*1000 with aggr interval 60 seconds   insert into rtc_idata_package_goldcoin_in_m select from_unixtime(AGGRTIME DIV 1000, 'yyyyMMddHHmm') vdatetime_m, bname vgame, packdim.pack_id ipackage, r ireason, sum(m) from (select bname, openid, r, m, agtime from wefeng_goldcoin_in ) utbl5 left join packdim on hash(concat(utbl5.openid, "-", utbl5.bname)) % 1000 = packdim.hash_id where bloom_contains(packdim.bloom, hash(concat(utbl5.openid, "-", utbl5.bname))) group by bname, packdim.pack_id, r coordinate by agtime*1000 with aggr interval 60 seconds   insert into rtc_idata_package_goldcoin_out_m select from_unixtime(AGGRTIME DIV 1000, 'yyyyMMddHHmm') vdatetime_m, bname vgame, packdim.pack_id ipackage, r ireason, sum(m) from (select bname, openid, r, m, agtime from wefeng_goldcoin_out ) utbl6 left join packdim on hash(concat(utbl6.openid, "-", utbl6.bname)) % 1000 = packdim.hash_id where bloom_contains(packdim.bloom, hash(concat(utbl6.openid, "-", utbl6.bname))) group by bname, packdim.pack_id, r coordinate by agtime*1000 with aggr interval 60 seconds 
            
            
            