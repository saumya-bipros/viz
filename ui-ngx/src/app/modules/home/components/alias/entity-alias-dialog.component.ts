import { Component, Inject, OnInit, SkipSelf } from '@angular/core';
import { ErrorStateMatcher } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormGroupDirective,
  NgForm,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';
import { DialogComponent } from '@app/shared/components/dialog.component';
import { EntityAlias, EntityAliases } from '@shared/models/alias.models';
import { AliasEntityType, EntityType } from '@shared/models/entity-type.models';
import { UtilsService } from '@core/services/utils.service';
import { TranslateService } from '@ngx-translate/core';
import { EntityService } from '@core/http/entity.service';
import { Observable } from 'rxjs';

export interface EntityAliasDialogData {
  isAdd: boolean;
  allowedEntityTypes: Array<EntityType | AliasEntityType>;
  entityAliases: EntityAliases | Array<EntityAlias>;
  alias?: EntityAlias;
}

@Component({
  selector: 'tb-entity-alias-dialog',
  templateUrl: './entity-alias-dialog.component.html',
  providers: [{provide: ErrorStateMatcher, useExisting: EntityAliasDialogComponent}],
  styleUrls: ['./entity-alias-dialog.component.scss']
})
export class EntityAliasDialogComponent extends DialogComponent<EntityAliasDialogComponent, EntityAlias>
  implements OnInit, ErrorStateMatcher {

  isAdd: boolean;
  allowedEntityTypes: Array<EntityType | AliasEntityType>;
  entityAliases: Array<EntityAlias>;

  alias: EntityAlias;

  entityAliasFormGroup: FormGroup;

  submitted = false;

  constructor(protected store: Store<AppState>,
              protected router: Router,
              @Inject(MAT_DIALOG_DATA) public data: EntityAliasDialogData,
              @SkipSelf() private errorStateMatcher: ErrorStateMatcher,
              public dialogRef: MatDialogRef<EntityAliasDialogComponent, EntityAlias>,
              private fb: FormBuilder,
              private utils: UtilsService,
              public translate: TranslateService,
              private entityService: EntityService) {
    super(store, router, dialogRef);
    this.isAdd = data.isAdd;
    this.allowedEntityTypes = data.allowedEntityTypes;
    if (Array.isArray(data.entityAliases)) {
      this.entityAliases = data.entityAliases;
    } else {
      this.entityAliases = [];
      for (const aliasId of Object.keys(data.entityAliases)) {
        this.entityAliases.push(data.entityAliases[aliasId]);
      }
    }
    if (this.isAdd && !this.data.alias) {
      this.alias = {
        id: null,
        alias: '',
        filter: {
          resolveMultiple: false
        }
      };
    } else {
      this.alias = data.alias;
    }

    this.entityAliasFormGroup = this.fb.group({
      alias: [this.alias.alias, [this.validateDuplicateAliasName(), Validators.required]],
      resolveMultiple: [this.alias.filter.resolveMultiple],
      filter: [this.alias.filter, Validators.required]
    });
  }

  validateDuplicateAliasName(): ValidatorFn {
    return (c: FormControl) => {
      const newAlias = c.value.trim();
      const found = this.entityAliases.find((entityAlias) => entityAlias.alias === newAlias);
      if (found) {
        if (this.isAdd || this.alias.id !== found.id) {
          return {
            duplicateAliasName: {
              valid: false
            }
          };
        }
      }
      return null;
    };
  }

  ngOnInit(): void {
  }

  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const originalErrorState = this.errorStateMatcher.isErrorState(control, form);
    const customErrorState = !!(control && control.invalid && this.submitted);
    return originalErrorState || customErrorState;
  }

  cancel(): void {
    this.dialogRef.close(null);
  }

  private validate(): Observable<any> {
    return this.entityService.resolveAliasFilter(this.alias.filter, null);
  }

  save(): void {
    this.submitted = true;
    this.alias.alias = this.entityAliasFormGroup.get('alias').value.trim();
    this.alias.filter = this.entityAliasFormGroup.get('filter').value;
    this.alias.filter.resolveMultiple = this.entityAliasFormGroup.get('resolveMultiple').value;
    if (this.alias.filter.type) {
      this.validate().subscribe(() => {
          if (this.isAdd) {
            this.alias.id = this.utils.guid();
          }
          this.dialogRef.close(this.alias);
        },
        () => {
          this.entityAliasFormGroup.setErrors({
            noEntityMatched: true
          });
          const changesSubscriptuion = this.entityAliasFormGroup.valueChanges.subscribe(() => {
            this.entityAliasFormGroup.setErrors(null);
            changesSubscriptuion.unsubscribe();
          });
        }
      );
    }
  }
}