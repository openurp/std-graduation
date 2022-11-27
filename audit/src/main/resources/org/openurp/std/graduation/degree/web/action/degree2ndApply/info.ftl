[#ftl]
[@b.toolbar title="${degree2ndApply.std.name}的二学位申请信息"]
  bar.addBack();
[/@]
[#assign std=degree2ndApply.std]
<table class="infoTable">
   <tr>
     <td class="title">姓名</td>
     <td>${std.name}</td>
     <td class="title">学号</td><td>${std.code}</td>
     <td class="title">性别</td><td>${(std.person.gender.name)!}</td>
     <td class="title">班级</td><td>${(std.state.squad.name)!}</td>
   </tr>
   <tr>
      <td class="title">所在学院</td><td>${std.state.department.name}</td>
      <td class="title">专业/方向</td><td>${(std.state.major.name)!} ${(std.state.direction.name)!}</td>
      <td class="title">入学时间</td><td>${(std.studyOn?string("yyyy年MM月dd日"))!}</td>
      <td class="title">毕业时间</td><td>${std.graduateOn?string("yyyy年MM月dd日")}</td>
   </tr>
   <tr>
      <td class="title">公共基础课绩点</td><td>${degree2ndApply.gpa?string("#.0000")}</td>
      <td class="title">学制</td><td>${std.duration}年</td>
      <td class="title">申请时间</td><td>${degree2ndApply.updatedAt?string("yyyy-MM-dd HH:mm")}</td>
      <td colspan="2"></td>
   </tr>
   <tr>
      <td class="title">公共基础课程成绩</td>
      <td colspan="7">
        <pre>${degree2ndApply.gradeDetail!}</pre>
      </td>
   </tr>
</table>
