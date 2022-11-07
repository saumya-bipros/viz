import * as React from 'react';
import VizzionnaireAceEditor from './json-form-ace-editor';
import { JsonFormFieldProps, JsonFormFieldState } from '@shared/components/json-form/react/json-form.models';
import { Observable } from 'rxjs/internal/Observable';
import { beautifyCss } from '@shared/models/beautify.models';

class VizzionnaireCss extends React.Component<JsonFormFieldProps, JsonFormFieldState> {

    constructor(props) {
        super(props);
        this.onTidyCss = this.onTidyCss.bind(this);
    }

    onTidyCss(css: string): Observable<string> {
        return beautifyCss(css, {indent_size: 4});
    }

    render() {
        return (
            <VizzionnaireAceEditor {...this.props} mode='css' onTidy={this.onTidyCss} {...this.state}></VizzionnaireAceEditor>
        );
    }
}

export default VizzionnaireCss;
