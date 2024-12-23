[#ftl]
[@b.head/]
[#assign margins = 19.05/]
[#assign PT = 0.35146/][#-- 1mm ≈ 0.35146 pt --]
[#function offset fontSize]
  [#return fontSize / 2.5 * 2/]
[/#function]
<style>
  div#authorized {
    width: ${210 - (margins + 0.3) * 2}mm;
    border-width: 1px;            [#-- 打印时，如果每页需要与首页格式一样，则必须加这 3 项设置 --]
    border-style: solid;
    border-color: transparent;

    padding: 0px;
    margin:auto;
  }

  div#authorized > div {
    width: 100%;
  }

  div#authorized > div#head {
    text-align: center;
    vertical-align: middle;
    margin-top: ${(22 + offset(22)) * 3 + (25.4 - margins) * PT + 5}pt;        [#-- 空 3 行，每行 22 pt（即：二号字体）；Word 中默认顶部页边距是 2.54 cm --]
    margin-bottom: ${(22 + offset(22)) * 2}pt;

    font-size: 22pt;
    font-weight: bold;
  }

  div#authorized > div.main {
    text-align: justify;
    text-justify: inter-ideograph;

    text-indent: 32pt;
    font-size: 16pt;
    line-height: 32pt;
  }

  div#authorized > div.main span.blod {
    font-weight: bold;
  }

  div#authorized > div.main span.char {
    margin-left: 1pt;
    margin-right: 1pt;
  }

  div#authorized > div.main span.highlight {
    font-weight: bold;
    margin-left: 4pt;
    margin-right: 4pt;
  }

  div#authorized > div#foot {
    text-align: center;
    margin-top: ${(15 + offset(15)) * 2}pt;      [#-- 空 2 行，每行 15 pt（即：三号字体） --]

    font-size: 16pt;
    line-height: 32pt;
    text-indent: ${11 * 20}pt;
  }

  div#authorized > div#foot_next {
    text-align: center;
    font-size: 16pt;
    line-height: 32pt;
    text-indent: ${11 * 20}pt;
  }
</style>
  <table id="bar"></table>
  [#list graduates as result]
  <div id="authorized" [#if result_has_next] style="page-break-after: always;"[/#if]>
    <div id="head">授予${result.std.project.category.name}${(result.degree.level.name)!}通知书</div>
    <div class="main"><span class="blod">${result.std.name}，学号：${result.std.code}，性别：${result.std.person.gender.name}，</span>系我校终身教育学院<span class="highlight">${result.std.state.major.name}</span>专业<span class="blod">${(result.std.state.squad.name)!}</span>毕业生。该生经我校学位评定委员会<span class="blod">${(result.degreeAwardOn?string("yyyy年M月d日"))!}</span>全体会议审核，符合《中华人民共和国学位条例》规定，授予成人高等教育<span class="highlight char">${(result.degree.name)!"？？？"}</span>学位。</div>
    <div class="main">特此通知。请将本通知存入本人档案。</div>
    <div id="foot">${result.std.project.school.name }</div>
    <div id="foot_next">${(result.degreeAwardOn?string("yyyy年M月d日"))!}</div>
  </div>
    [/#list]
  <script>
    var bar = new ToolBar("bar", "授权学位证书", null, true, true);
    bar.addPrint();
    bar.addClose();
  </script>
[@b.foot/]
