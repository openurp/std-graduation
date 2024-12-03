[#ftl]
[@b.head/]
  [@b.grid items=settings! var="setting"]
    [@b.gridbar]
          bar.addItem("${b.text("action.new")}", action.add());
          bar.addItem("修改", action.edit());
          bar.addItem("删除", action.remove());
      [/@]
    [@b.row]
      [@b.boxcol width="5%"/]
      [@b.col title="名称" property="name" width="15%"]
        [@b.a href="!info?id=${setting.id}" title="查看详情"]${setting.name!}[/@]
      [/@]
          [@b.col title="培养层次" width="10%"]
            [#list setting.levels! as s]${s.name}[#if s_has_next]&nbsp;[/#if][/#list]
          [/@]
          [@b.col title="毕业审核项目" width="10%"]
            [#list grules.get(setting)! as rule]
              ${rule.title}
          [#if rule.params?size > 0]
          (
            [#list rule.params as k,v]
              ${k} = ${v}
              [#if k_has_next], [/#if]
            [/#list]
          )
          [/#if]
          [#if rule_has_next];[/#if]
            [/#list]
          [/@]
          [@b.col title="学位审核项目"]
            [#list drules.get(setting) as rule]
              ${rule.title}
          [#if rule.params?size > 0]
          (
            [#list rule.params as k,v]
              ${k} = ${v}
              [#if k_has_next], [/#if]
            [/#list]
          )
          [/#if]
          [#if rule_has_next];[/#if]
            [/#list]
          [/@]
          [@b.col title="有效期限" property="beginOn" width="10%"]${setting.beginOn?string("yyyy-MM-dd")}~${(setting.endOn?string("yyyy-MM-dd"))!}[/@]
      [/@]
  [/@]
