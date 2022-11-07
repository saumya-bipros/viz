import {
  AfterViewInit,
  Component,
  Directive,
  ElementRef,
  Inject,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
  ViewEncapsulation
} from '@angular/core';
import { WINDOW } from '@core/services/window.service';
import { CanColorCtor, mixinColor } from '@angular/material/core';
import { ResizeObserver } from '@juggle/resize-observer';

export declare type FabToolbarDirection = 'left' | 'right';

class MatFabToolbarBase {
  // tslint:disable-next-line:variable-name
  constructor(public _elementRef: ElementRef) {}
}
const MatFabToolbarMixinBase: CanColorCtor & typeof MatFabToolbarBase = mixinColor(MatFabToolbarBase);

@Directive({
  // tslint:disable-next-line:directive-selector
  selector: 'mat-fab-trigger'
})
export class FabTriggerDirective {

  constructor(private el: ElementRef<HTMLElement>) {
  }

}

@Directive({
  // tslint:disable-next-line:directive-selector
  selector: 'mat-fab-actions'
})
export class FabActionsDirective implements OnInit {

  constructor(private el: ElementRef<HTMLElement>) {
  }

  ngOnInit(): void {
    const element = $(this.el.nativeElement);
    const children = element.children();
    children.wrap('<div class="mat-fab-action-item">');
  }

}

// @dynamic
@Component({
  // tslint:disable-next-line:component-selector
  selector: 'mat-fab-toolbar',
  templateUrl: './fab-toolbar.component.html',
  styleUrls: ['./fab-toolbar.component.scss'],
  inputs: ['color'],
  encapsulation: ViewEncapsulation.None
})
export class FabToolbarComponent extends MatFabToolbarMixinBase implements OnInit, OnDestroy, AfterViewInit, OnChanges {

  private fabToolbarResize$: ResizeObserver;

  @Input()
  isOpen: boolean;

  @Input()
  direction: FabToolbarDirection;

  constructor(private el: ElementRef<HTMLElement>,
              @Inject(WINDOW) private window: Window) {
    super(el);
  }

  ngOnInit(): void {
    const element = $(this.el.nativeElement);
    element.addClass('mat-fab-toolbar');
    element.find('mat-fab-trigger').find('button')
      .prepend('<div class="mat-fab-toolbar-background"></div>');
    element.addClass(`mat-${this.direction}`);
    this.fabToolbarResize$ = new ResizeObserver(() => {
      this.onFabToolbarResize();
    });
    this.fabToolbarResize$.observe(this.el.nativeElement);
  }

  ngOnDestroy(): void {
    this.fabToolbarResize$.disconnect();
  }

  ngAfterViewInit(): void {
    this.triggerOpenClose(true);
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const propName of Object.keys(changes)) {
      const change = changes[propName];
      if (!change.firstChange && change.currentValue !== change.previousValue) {
        if (propName === 'isOpen') {
          this.triggerOpenClose();
        }
      }
    }
  }

  private onFabToolbarResize() {
    if (this.isOpen) {
      this.triggerOpenClose(true);
    }
  }

  private triggerOpenClose(disableAnimation?: boolean): void {
    const el = this.el.nativeElement;
    const element = $(this.el.nativeElement);
    if (disableAnimation) {
      element.removeClass('mat-animation');
    } else {
      element.addClass('mat-animation');
    }
    const backgroundElement: HTMLElement = el.querySelector('.mat-fab-toolbar-background');
    const triggerElement: HTMLElement = el.querySelector('mat-fab-trigger button');
    const toolbarElement: HTMLElement = el.querySelector('mat-toolbar');
    const iconElement: HTMLElement = el.querySelector('mat-fab-trigger button mat-icon');
    const actions = element.find<HTMLElement>('mat-fab-actions').children();
    if (triggerElement && backgroundElement) {
      const width = el.offsetWidth;
      const scale = 2 * (width / triggerElement.offsetWidth);

      backgroundElement.style.borderRadius = width + 'px';

      if (this.isOpen) {
        element.addClass('mat-is-open');
        toolbarElement.style.pointerEvents = 'inherit';

        backgroundElement.style.width = triggerElement.offsetWidth + 'px';
        backgroundElement.style.height = triggerElement.offsetHeight + 'px';
        backgroundElement.style.transform = 'scale(' + scale + ')';

        backgroundElement.style.transitionDelay = '0ms';
        if (iconElement) {
          iconElement.style.transitionDelay = disableAnimation ? '0ms' : '.3s';
        }

        actions.each((index, action) => {
          action.style.transitionDelay = disableAnimation ? '0ms' : ((actions.length - index) * 25 + 'ms');
        });

      } else {
        element.removeClass('mat-is-open');
        toolbarElement.style.pointerEvents = 'none';

        backgroundElement.style.transform = 'scale(1)';

        backgroundElement.style.top = '0';

        if (element.hasClass('mat-right')) {
          backgroundElement.style.left = '0';
          backgroundElement.style.right = null;
        }

        if (element.hasClass('mat-left')) {
          backgroundElement.style.right = '0';
          backgroundElement.style.left = null;
        }

        backgroundElement.style.transitionDelay = disableAnimation ? '0ms' : '200ms';

        actions.each((index, action) => {
          action.style.transitionDelay = (disableAnimation ? 0 : 200) + (index * 25) + 'ms';
        });
      }
    }
  }

}