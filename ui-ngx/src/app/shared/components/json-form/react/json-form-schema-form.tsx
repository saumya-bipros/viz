import * as React from 'react';
import JsonFormUtils from './json-form-utils';

import VizzionnaireArray from './json-form-array';
import VizzionnaireJavaScript from './json-form-javascript';
import VizzionnaireJson from './json-form-json';
import VizzionnaireHtml from './json-form-html';
import VizzionnaireCss from './json-form-css';
import VizzionnaireColor from './json-form-color';
import VizzionnaireRcSelect from './json-form-rc-select';
import VizzionnaireNumber from './json-form-number';
import VizzionnaireText from './json-form-text';
import VizzionnaireSelect from './json-form-select';
import VizzionnaireRadios from './json-form-radios';
import VizzionnaireDate from './json-form-date';
import VizzionnaireImage from './json-form-image';
import VizzionnaireCheckbox from './json-form-checkbox';
import VizzionnaireHelp from './json-form-help';
import VizzionnaireFieldSet from './json-form-fieldset';
import VizzionnaireIcon from './json-form-icon';
import {
  JsonFormData,
  JsonFormProps,
  onChangeFn,
  OnColorClickFn, onHelpClickFn,
  OnIconClickFn,
  onToggleFullscreenFn
} from './json-form.models';

import _ from 'lodash';
import * as tinycolor_ from 'tinycolor2';
import { GroupInfo } from '@shared/models/widget.models';
import VizzionnaireMarkdown from '@shared/components/json-form/react/json-form-markdown';
import { MouseEvent } from 'react';

const tinycolor = tinycolor_;

class VizzionnaireSchemaForm extends React.Component<JsonFormProps, any> {

  private hasConditions: boolean;
  private readonly mapper: {[type: string]: any};

  constructor(props) {
    super(props);

    this.mapper = {
      number: VizzionnaireNumber,
      text: VizzionnaireText,
      password: VizzionnaireText,
      textarea: VizzionnaireText,
      select: VizzionnaireSelect,
      radios: VizzionnaireRadios,
      date: VizzionnaireDate,
      image: VizzionnaireImage,
      checkbox: VizzionnaireCheckbox,
      help: VizzionnaireHelp,
      array: VizzionnaireArray,
      javascript: VizzionnaireJavaScript,
      json: VizzionnaireJson,
      html: VizzionnaireHtml,
      css: VizzionnaireCss,
      markdown: VizzionnaireMarkdown,
      color: VizzionnaireColor,
      'rc-select': VizzionnaireRcSelect,
      fieldset: VizzionnaireFieldSet,
      icon: VizzionnaireIcon
    };

    this.onChange = this.onChange.bind(this);
    this.onColorClick = this.onColorClick.bind(this);
    this.onIconClick = this.onIconClick.bind(this);
    this.onToggleFullscreen = this.onToggleFullscreen.bind(this);
    this.onHelpClick = this.onHelpClick.bind(this);
    this.hasConditions = false;
  }

  onChange(key: (string | number)[], val: any, forceUpdate?: boolean) {
    this.props.onModelChange(key, val, forceUpdate);
    if (this.hasConditions) {
      this.forceUpdate();
    }
  }

  onColorClick(key: (string | number)[], val: tinycolor.ColorFormats.RGBA,
               colorSelectedFn: (color: tinycolor.ColorFormats.RGBA) => void) {
    this.props.onColorClick(key, val, colorSelectedFn);
  }

  onIconClick(key: (string | number)[], val: string,
              iconSelectedFn: (icon: string) => void) {
    this.props.onIconClick(key, val, iconSelectedFn);
  }

  onToggleFullscreen(fullscreenFinishFn?: (el: Element) => void) {
    this.props.onToggleFullscreen(fullscreenFinishFn);
  }

  onHelpClick(event: MouseEvent, helpId: string, helpVisibleFn: (visible: boolean) => void, helpReadyFn: (ready: boolean) => void) {
    this.props.onHelpClick(event, helpId, helpVisibleFn, helpReadyFn);
  }


  builder(form: JsonFormData,
          model: any,
          index: number,
          onChange: onChangeFn,
          onColorClick: OnColorClickFn,
          onIconClick: OnIconClickFn,
          onToggleFullscreen: onToggleFullscreenFn,
          onHelpClick: onHelpClickFn,
          mapper: {[type: string]: any}): JSX.Element {
    const type = form.type;
    const Field = this.mapper[type];
    if (!Field) {
      console.log('Invalid field: \"' + form.key[0] + '\"!');
      return null;
    }
    if (form.condition) {
      this.hasConditions = true;
      // tslint:disable-next-line:no-eval
      if (eval(form.condition) === false) {
        return null;
      }
    }
    return <Field model={model} form={form} key={index} onChange={onChange}
                  onColorClick={onColorClick}
                  onIconClick={onIconClick}
                  onToggleFullscreen={onToggleFullscreen}
                  onHelpClick={onHelpClick}
                  mapper={mapper} builder={this.builder}/>;
  }

  createSchema(theForm: any[]): JSX.Element {
    const merged = JsonFormUtils.merge(this.props.schema, theForm, this.props.ignore, this.props.option);
    let mapper = this.mapper;
    if (this.props.mapper) {
      mapper = _.merge(this.mapper, this.props.mapper);
    }
    const forms = merged.map(function(form, index) {
      return this.builder(form, this.props.model, index, this.onChange, this.onColorClick,
        this.onIconClick, this.onToggleFullscreen, this.onHelpClick, mapper);
    }.bind(this));

    let formClass = 'SchemaForm';
    if (this.props.isFullscreen) {
      formClass += ' SchemaFormFullscreen';
    }

    return (
      <div style={{width: '100%'}} className={formClass}>{forms}</div>
    );
  }

  render() {
    if (this.props.groupInfoes && this.props.groupInfoes.length > 0) {
      const content: JSX.Element[] = [];
      for (const info of this.props.groupInfoes) {
        const forms = this.createSchema(this.props.form[info.formIndex]);
        const item = <VizzionnaireSchemaGroup key={content.length} forms={forms} info={info}></VizzionnaireSchemaGroup>;
        content.push(item);
      }
      return (<div>{content}</div>);
    } else {
      return this.createSchema(this.props.form);
    }
  }
}
export default VizzionnaireSchemaForm;

interface VizzionnaireSchemaGroupProps {
  info: GroupInfo;
  forms: JSX.Element;
}

interface VizzionnaireSchemaGroupState {
  showGroup: boolean;
}

class VizzionnaireSchemaGroup extends React.Component<VizzionnaireSchemaGroupProps, VizzionnaireSchemaGroupState> {
  constructor(props) {
    super(props);
    this.state = {
      showGroup: true
    };
  }

  toogleGroup(index) {
    this.setState({
      showGroup: !this.state.showGroup
    });
  }

  render() {
    const theCla = 'pull-right fa fa-chevron-down tb-toggle-icon' + (this.state.showGroup ? '' : ' tb-toggled');
    return (<section className='mat-elevation-z1' style={{marginTop: '10px'}}>
      <div className='SchemaGroupname tb-button-toggle'
           onClick={this.toogleGroup.bind(this)}>{this.props.info.GroupTitle}<span className={theCla}></span></div>
      <div style={{padding: '20px'}} className={this.state.showGroup ? '' : 'invisible'}>{this.props.forms}</div>
    </section>);
  }
}
