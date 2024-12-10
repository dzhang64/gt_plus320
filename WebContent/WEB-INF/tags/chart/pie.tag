<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/webpage/include/taglib.jsp"%>

<%@ attribute name="pieData" type="java.util.HashMap" required="true" description="饼图数据"%>

<div id="pie" style="width: 100%; height:300%;"></div>
<script src="${ctxStatic }/common/echarts.js"></script>
<script type="text/javascript">
var myChart = echarts.init(document.getElementById('pie'));
myChart.setOption({
    title : {
        text: '${pieData.title}',
        subtext: '${pieData.subTitle}',
        x:'center'
    },
    tooltip : {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)"
    },
    legend: {
        orient: 'vertical',
        left: 'left',
        data: ${pieData.legend}
    },
    series : [
        {
            name: '${pieData.describe}',
            type: 'pie',
            radius : '55%',
            center: ['50%', '60%'],
            data: ${pieData.series},
            itemStyle: {
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }
    ]
});
</script>