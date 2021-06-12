[#ftl]
[@b.head/]
[@b.grid items=squads var="squad"]
  [@b.gridbar]
    bar.addItem("学生签收表",action.multi('signatureReport',null,null,"_blank"));
    bar.addItem("证书明细",action.multi('detail',null,null,"_blank"));
    bar.addItem("班级签收",action.method('report',null,null,"_blank"));
  [/@]
  [@b.row]
    [@b.boxcol/]
    [@b.col title="序号" width="5%"]${squad_index+1}[/@]
    [@b.col title="院系" width="15%" property="department.name"/]
    [@b.col title="年级" width="10%" property="grade"/]
    [@b.col title="专业" width="25%"]
    ${(squad.major.name)!} ${(squad.direction.name)!}
    [/@]
    [@b.col title="班级" width="30%" property="name"]
     [@b.a href="!detail?session.id=${graduateSession.id}&squadIds=${squad.id}&batch=${Parameters['batch']!}" target="_blank"]${squad.name}[/@]
    [/@]
    [@b.col title="人数" width="10%"]
      ${squadMap.get(squad)}
    [/@]
  [/@]
[/@]
[@b.foot/]
