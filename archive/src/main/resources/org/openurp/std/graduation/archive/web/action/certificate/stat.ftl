[#ftl]
[@b.head/]
<div class="container">
   <H4 width="95%" align="center" style="margin-bottom:5px">${graduateBatch.graduateOn?string('YYYY')}年${graduateBatch.graduateOn?string('MM')}月毕业证书签收表</H4>
   <table class="gridtable" align="center" style="border:1px">
     <thead  class="gridhead">
       <tr class="darkColumn" style="height:26px;font-weight:bolder;" align="center">
         <td width="4%">序号</td>
         <td width="13%">院系</td>
         <td width="37%">班级</td>
         <td width="6%">人数</td>
         <td width="10%">签收</td>
         <td width="15%">日期</td>
         <td width="10%">辅导员</td>
         <td width="10%">备注</td>
       </tr>
     </thead>
     <tbody>
     [#assign lastDepart=""]
     [#assign totalInDepart=0]
     [#list squads as squad]
       [#if lastDepart?length ==0 ]
        [#assign lastDepart=squad.department.name]
       [/#if]
       [#if squad.department.name != lastDepart]
       <tr style="height:26px" class="brightStyle">
         <td colspan="6"></td>
         <td>小计:${totalInDepart}人</td>
         [#assign totalInDepart=0]
         [#assign lastDepart=squad.department.name]
       </tr>
       [/#if]
       <tr style="height:26px" class="brightStyle">
         <td  align="center">${squad_index+1}</td>
         <td  align="center">${squad.department.name}</td>
         <td>${squad.name}</td>
         <td align="center">${squadMap.get(squad)} [#assign totalInDepart=totalInDepart+squadMap.get(squad)]</td>
         <td></td>
         <td></td>
         <td>${(squad.master.name)!}</td>
         <td></td>
       </tr>

       [/#list]
       [#if lastDepart?length !=0]
       <tr style="height:26px" class="brightStyle">
         <td colspan="6"></td>
         <td>小计:${totalInDepart}人</td>
         <td></td>
       </tr>
       [/#if]
     </tbody>
   </table>
</div>
[@b.foot/]
