import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RuleChainService } from '@core/http/rule-chain.service';
import { switchMap } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import {
  NodeScriptTestDialogComponent,
  NodeScriptTestDialogData
} from '@shared/components/dialog/node-script-test-dialog.component';
import { sortObjectKeys } from '@core/utils';

@Injectable({
  providedIn: 'root'
})
export class NodeScriptTestService {

  constructor(private ruleChainService: RuleChainService,
              public dialog: MatDialog) {
  }

  testNodeScript(script: string, scriptType: string, functionTitle: string,
                 functionName: string, argNames: string[], ruleNodeId: string, helpId?: string): Observable<string> {
    if (ruleNodeId) {
      return this.ruleChainService.getLatestRuleNodeDebugInput(ruleNodeId).pipe(
        switchMap((debugIn) => {
          let msg: any;
          let metadata: {[key: string]: string};
          let msgType: string;
          if (debugIn) {
            if (debugIn.data) {
              msg = JSON.parse(debugIn.data);
            }
            if (debugIn.metadata) {
              metadata = JSON.parse(debugIn.metadata);
            }
            msgType = debugIn.msgType;
          }
          return this.openTestScriptDialog(script, scriptType, functionTitle,
            functionName, argNames, msg, metadata, msgType, helpId);
        })
      );
    } else {
      return this.openTestScriptDialog(script, scriptType, functionTitle,
        functionName, argNames, null, null, null, helpId);
    }
  }

  private openTestScriptDialog(script: string, scriptType: string,
                               functionTitle: string, functionName: string, argNames: string[],
                               msg?: any, metadata?: {[key: string]: string}, msgType?: string, helpId?: string): Observable<string> {
    if (!msg) {
      msg = {
        temperature: 22.4,
        humidity: 78
      };
    }
    if (!metadata) {
      metadata = {
        deviceName: 'Test Device',
        deviceType: 'default',
        ts: new Date().getTime() + ''
      };
    } else {
      metadata = sortObjectKeys(metadata);
    }
    if (!msgType) {
      msgType = 'POST_TELEMETRY_REQUEST';
    }
    return this.dialog.open<NodeScriptTestDialogComponent, NodeScriptTestDialogData, string>(NodeScriptTestDialogComponent,
      {
        disableClose: true,
        panelClass: ['tb-dialog', 'tb-fullscreen-dialog', 'tb-fullscreen-dialog-gt-xs'],
        data: {
          msg,
          metadata,
          msgType,
          functionTitle,
          functionName,
          script,
          scriptType,
          argNames,
          helpId
        }
      }).afterClosed();
  }

}
