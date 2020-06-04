[#assign std=degreeApply.std]
<table class="infoTable">
   <tr>
     <td class="title">姓名</td>
     <td>${std.user.name}</td>
     <td class="title">性别</td><td>${(std.person.gender.name)!}</td>
     <td class="title">班级</td><td>${(std.state.squad.name)!}</td>
     <td class="title">学号</td><td>${std.user.code}</td>
   </tr>
   <tr>
      <td class="title">所在学院</td><td>${std.state.department.name}</td>
      <td class="title">专业</td><td>${(std.state.major.name)!}</td>
      <td class="title">方向</td><td>${(std.state.direction.name)!}</td>
      <td colspan="2"></td>
   </tr>
   <tr>
      <td class="title">入学时间</td><td>${(std.studyOn?string("yyyy年MM月dd日"))!}</td>
      <td class="title">毕业时间</td><td>${std.graduateOn?string("yyyy年MM月dd日")}</td>
      <td class="title">学制</td><td>${std.duration}年</td>
      <td colspan="2"></td>
   </tr>
   <tr>
      <td class="title">申请何种学位</td><td>${degreeApply.degree.name}</td>
      <td class="title">申请学位时间</td><td>${degreeApply.updatedAt?string("yyyy年MM月dd日")}</td>
      <td colspan="4"></td>
   </tr>
</table>