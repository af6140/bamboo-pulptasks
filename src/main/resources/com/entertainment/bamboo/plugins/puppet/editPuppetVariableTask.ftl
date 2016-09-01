
[@ui.bambooSection title='Variable Prefix' ]
        [@ww.radio label='Variable Prefix' name='prefixOption'
                   listKey='key' listValue='value' toggle='true'
                   list=prefixOptions ]
        [/@ww.radio]
        [@ui.bambooSection dependsOn='prefixOption' showOn='0']
            [@ww.textfield label='Custom Variable Prefix' name='customPrefix' /]
        [/@ui.bambooSection]
[/@ui.bambooSection]

[@ww.radio label="Variable Type" name='variableType'
	           listKey='key' listValue='value' toggle='true'
	           list=variableTypeOptions ]
[/@ww.radio]

[@ui.bambooSection title='Relative path metadata.json (relative to work directory)']
	[@ww.textfield name='metaJSON' cssClass="long-field" /]
[/@ui.bambooSection]
