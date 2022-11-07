import * as React from 'react';
import VizzionnaireAceEditor from './json-form-ace-editor';
import { JsonFormFieldProps, JsonFormFieldState } from '@shared/components/json-form/react/json-form.models';
import { Observable } from 'rxjs/internal/Observable';
import { beautifyJs } from '@shared/models/beautify.models';

class VizzionnaireJson extends React.Component<JsonFormFieldProps, JsonFormFieldState> {

    constructor(props) {
        super(props);
        this.onTidyJson = this.onTidyJson.bind(this);
    }

    onTidyJson(json: string): Observable<string> {
        return beautifyJs(json, {indent_size: 4});
    }

    render() {
        return (
            <VizzionnaireAceEditor {...this.props} mode='json' onTidy={this.onTidyJson} {...this.state}></VizzionnaireAceEditor>
        );
    }
}

export default VizzionnaireJson;
