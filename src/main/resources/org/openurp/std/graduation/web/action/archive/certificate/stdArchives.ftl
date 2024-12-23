[#ftl]
[@b.head/]
    [@b.form method="post" action="!stdArchives" style="text-align: right;margin-bottom:0px"]
       <h3 width="100%" align="center" style="margin-bottom:0px">${graduateBatch.project.school.name}${graduateBatch.graduateOn?string('yyyy-MM-dd')}${archiveCategory}档案明细(${startArchiveNo}~${startArchiveNo+squads?size})</h3>
       <input type="hidden" name="batch.id" value="${graduateBatch.id}"/>
       <label for="archiveCategory">归档内容:</label><input type="text" name="archiveCategory" id="archiveCategory" value="${archiveCategory}" style="width:120px"/>
       <label for="startArchiveNo">案卷开始号:</label><input type="text" name="startArchiveNo" id="startArchiveNo" value="${startArchiveNo}" style="width:30px"/>
       <label for="archiver">移交人:</label><input type="text" name="archiver" id="archiver" value="${archiver}" style="width:50px"/>
       <label for="archiveOn">归档时间:</label><input type="text" name="archiveOn" id="archiveOn" value="${archiveOn?string("yyyy-MM-dd")}"  style="width:90px"/>
       <input type="submit" value="提交">
    [/@]
  <div class="container-fluid">
    <table class="grid-table" width="100%">
     <thead class="grid-head">
       <tr>
         <th width="5%">案卷号及件</th>
         <th width="7%">责任者</th>
         <th width="43%">正题名</th>
         <th width="12%">起止时间</th>
         <th width="4%">页数_页码</th>
         <th width="4%">保管期限</th>
         <th width="4%">密级</th>
         <th width="7%">归档单位</th>
         <th width="6%">归档时间</th>
         <th width="5%">移交人</th>
         <th width="3%">备注</th>
       </tr>
     </thead>
     <tbody class="grid-body">
     [#assign archiveNo=startArchiveNo/]
     [#if overGraduated?size &gt; 0]
       [#assign arList = overGraduated?sort_by(["std","code"])]
       [#assign first = arList?first/]
       <tr>
         <td>${archiveNo}</td>
         <td>${first.std.college.name}</td>
         <td style="text-align:left;padding-left:5px;">${graduateBatch.graduateOn?string('yyyy')}届遗留延长${archiveCategory}</td>
         <td>${first.std.beginOn?string('yyyy.MM.dd')}-${first.graduateOn?string('yyyy.MM.dd')}</td>
         <td>${arList?size}</td>
         <td>永久</td>
         <td>公开</td>
         <td>${first.std.college.name}</td>
         <td>${archiveOn?string("yyyyMMdd")}</td>
         <td>${archiver}</td>
         <td></td>
       </tr>
      [#list arList as ar]
       <tr>
         <td>${archiveNo}.${(ar_index+1)?string("0000")}</td>
         <td>${ar.std.college.name}</td>
         <td style="text-align:left;padding-left:5px;">${ar.std.squad.name} ${ar.std.name} （${ar.std.code}）${archiveCategory}</td>
         <td>${ar.std.beginOn?string('yyyy.MM.dd')}-${ar.graduateOn?string('yyyy.MM.dd')}</td>
         <td>${ar_index+1}</td>
         <td>永久</td>
         <td>公开</td>
         <td>${ar.std.college.name}</td>
         <td>${archiveOn?string("yyyyMMdd")}</td>
         <td>${archiver}</td>
         <td></td>
       </tr>
       [/#list]
       [#assign archiveNo =archiveNo +1]
     [/#if]
     [#list squads as adc]
       [#assign arList = res[adc.id?string]?sort_by(["std","code"])]
       <tr>
         <td>${archiveNo}</td>
         <td>${arList?first.std.college.name}</td>
         <td style="text-align:left;padding-left:5px;">${adc.name}${archiveCategory}</td>
         <td>${arList?first.std.beginOn?string('yyyy.MM.dd')}-${arList?first.graduateOn?string('yyyy.MM.dd')}</td>
         <td>${arList?size}</td>
         <td>永久</td>
         <td>公开</td>
         <td>${arList?first.std.college.name}</td>
         <td>${archiveOn?string("yyyyMMdd")}</td>
         <td>${archiver}</td>
         <td></td>
       </tr>
      [#list arList as ar]
       <tr>
         <td>${archiveNo}.${(ar_index+1)?string("0000")}</td>
         <td>${ar.std.college.name}</td>
         <td style="text-align:left;padding-left:5px;">${(ar.std.state.squad.name)!} ${ar.std.name} （${ar.std.code}）${archiveCategory}</td>
         <td>${ar.std.beginOn?string('yyyy.MM.dd')}-${ar.graduateOn?string('yyyy.MM.dd')}</td>
         <td>${ar_index+1}</td>
         <td>永久</td>
         <td>公开</td>
         <td>${ar.std.college.name}</td>
         <td>${archiveOn?string("yyyyMMdd")}</td>
         <td>${archiver}</td>
         <td></td>
       </tr>
       [/#list]
       [#assign archiveNo =archiveNo +1]
      [/#list]
    </tbody>
  </table>
  </div>
[@b.foot/]
