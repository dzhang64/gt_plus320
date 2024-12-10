<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/webpage/include/taglib.jsp"%>

<%@ attribute name="annularData" type="java.util.HashMap" required="true" description="环图数据"%>

<div id="annular" style="width: 100%; height:300%;"></div>
<script src="${ctxStatic }/common/echarts.js"></script>
<script type="text/javascript">
var myChart = echarts.init(document.getElementById('annular'));
myChart.setOption({
	title: {
		text: '${annularData.title}',
		subtext: '${annularData.subTitle}',
		x: 'left'
	},
    tooltip: {
        trigger: 'item',
        formatter: "{a} <br/>{b}: {c} ({d}%)"
    },
    legend: {
        orient: 'vertical',
        x: 'left',
        y: 'bottom',
        data:${annularData.legend}
    },
    series: [
        {
            name:'${annularData.describe}',
            type:'pie',
            radius: ['60%', '80%'],
            avoidLabelOverlap: false,
            label: {
                normal: {
                    show: false,
                    position: 'center'
                },
                emphasis: {
                    show: true,
                    textStyle: {
                        fontSize: '30',
                        fontWeight: 'bold'
                    }
                }
            },
            labelLine: {
                normal: {
                    show: false
                }
            },
            data:${annularData.series}
        }
    ]
});
</script>