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
