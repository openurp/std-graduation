[#ftl/]
[@b.head/]
[@b.toolbar title="毕业审核标准详情"]
  bar.addBack();
[/@]
[#include "../../../../chosenOptions.ftl" /]

[@b.form action="!save" name="standardForm" id="standardForm" theme="list"]
  [@b.field label="名称"]${standard.name}[/@]
  [@b.field label="有效期"]
    ${standard.beginOn?string('yyyy-MM-dd')}~${(standard.endOn?string('yyyy-MM-dd'))!}
  [/@]
  [@b.field label="培养层次"]
    [#list standard.studentScope.levels! as bc]
    ${bc.name}[#if bc_has_next],&nbsp;[/#if]
    [/#list]
  [/@]
  [@b.field label="学生类别"]
    [#list standard.studentScope.stdTypes! as bc]
    ${bc.name}[#if bc_has_next],&nbsp;[/#if]
    [/#list]
  [/@]
  [@b.field label="审核项目"]
  <div style="display:inline-block">
    [#list standard.ruleConfigs! as  ruleConfig]
      ${ruleConfig.rule.name}
      [#if ruleConfig.params?size > 0]
      (
        [#list ruleConfig.params as rcp]
        ${rcp.param.title} = ${rcp.value}
        [#if rcp_has_next]&nbsp;[/#if]
        [/#list]
      )
      [/#if]
      [#if ruleConfig_has_next]<br>[/#if]
    [/#list]
  </div>
  [/@]
[/@]
[@b.foot/]
