[#ftl]
[@b.head/]
[@b.grid items=squads var="squad"]
  [@b.gridbar]
    bar.addItem("学生签收表",action.multi('signature',null,null,"_blank"));
    bar.addItem("证书明细",action.multi('detail',null,null,"_blank"));
    bar.addItem("班级汇总",action.method('stat',null,null,"_blank"));
    bar.addItem("延长生签收表",action.method('deferred',null,null,"_blank"));
  [/@]
  [@b.row]
    [@b.boxcol/]
    [@b.col title="序号" width="5%"]${squad_index+1}[/@]
    [@b.col title="年级" width="10%" property="grade"/]
    [@b.col title="学历层次" width="10%" property="level.name"/]
    [@b.col title="院系" width="15%" property="department.name"/]
    [@b.col title="专业" width="15%"]
    ${(squad.major.name)!} ${(squad.direction.name)!}
    [/@]
    [@b.col title="班级" width="30%" property="name"]
     [@b.a href="!signature?batch.id=${graduateBatch.id}&squadIds=${squad.id}&batchNo=${Parameters['batchNo']!}" target="_blank"]${squad.name}[/@]
    [/@]
    [@b.col title="人数" width="10%"]
      ${squadMap.get(squad)}
    [/@]
  [/@]
[/@]
[@b.foot/]
