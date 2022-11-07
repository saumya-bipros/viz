import * as React from 'react';
import VizzionnaireAceEditor from './json-form-ace-editor';
import { JsonFormFieldProps, JsonFormFieldState } from '@shared/components/json-form/react/json-form.models';

class VizzionnaireMarkdown extends React.Component<JsonFormFieldProps, JsonFormFieldState> {

  constructor(props) {
    super(props);
  }

  render() {
    return (
      <VizzionnaireAceEditor {...this.props} mode='markdown' {...this.state}></VizzionnaireAceEditor>
    );
  }
}

export default VizzionnaireMarkdown;
