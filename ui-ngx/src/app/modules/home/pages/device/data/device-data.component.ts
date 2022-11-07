import { Component, forwardRef, Input, OnInit } from '@angular/core';
import {
  ControlValueAccessor,
  FormBuilder,
  FormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import {
  DeviceData,
  deviceProfileTypeConfigurationInfoMap,
  deviceTransportTypeConfigurationInfoMap
} from '@shared/models/device.models';

@Component({
  selector: 'tb-device-data',
  templateUrl: './device-data.component.html',
  styleUrls: [],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DeviceDataComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => DeviceDataComponent),
      multi: true
    },
  ]
})
export class DeviceDataComponent implements ControlValueAccessor, OnInit, Validator {

  deviceDataFormGroup: FormGroup;

  private requiredValue: boolean;
  get required(): boolean {
    return this.requiredValue;
  }
  @Input()
  set required(value: boolean) {
    this.requiredValue = coerceBooleanProperty(value);
  }

  @Input()
  disabled: boolean;

  displayDeviceConfiguration: boolean;
  displayTransportConfiguration: boolean;

  private propagateChange = (v: any) => { };

  constructor(private store: Store<AppState>,
              private fb: FormBuilder) {
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  ngOnInit() {
    this.deviceDataFormGroup = this.fb.group({
      configuration: [null, Validators.required],
      transportConfiguration: [null, Validators.required]
    });
    this.deviceDataFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.deviceDataFormGroup.disable({emitEvent: false});
    } else {
      this.deviceDataFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: DeviceData | null): void {
    const deviceProfileType = value?.configuration?.type;
    this.displayDeviceConfiguration = deviceProfileType &&
      deviceProfileTypeConfigurationInfoMap.get(deviceProfileType).hasDeviceConfiguration;
    const deviceTransportType = value?.transportConfiguration?.type;
    this.displayTransportConfiguration = deviceTransportType &&
      deviceTransportTypeConfigurationInfoMap.get(deviceTransportType).hasDeviceConfiguration;
    this.deviceDataFormGroup.patchValue({configuration: value?.configuration}, {emitEvent: false});
    this.deviceDataFormGroup.patchValue({transportConfiguration: value?.transportConfiguration}, {emitEvent: false});
  }

  validate(): ValidationErrors | null {
    return this.deviceDataFormGroup.valid ? null : {
      deviceDataForm: false
    };
  }

  private updateModel() {
    let deviceData: DeviceData = null;
    if (this.deviceDataFormGroup.valid) {
      deviceData = this.deviceDataFormGroup.getRawValue();
    }
    this.propagateChange(deviceData);
  }
}
