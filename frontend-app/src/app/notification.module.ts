//all of the configuration for the notification pop up inside of the UI, then import inside of app.module
//default config for notiferoptions from website besides position and vertical

import { NotifierModule, NotifierOptions } from 'angular-notifier';
import { NgModule } from '@angular/core';

const notifierDefaultOptions: NotifierOptions = {
  position: {
    horizontal: {
      position: 'left',
      distance: 150,
    },
    vertical: {
      position: 'top',
      distance: 12,
      gap: 10,
    },
  },
  theme: 'material',
  behaviour: {
    autoHide: 5000,
    onClick: 'hide',
    onMouseover: 'pauseAutoHide',
    showDismissButton: true,
    stacking: 4,
  },
  animations: {
    enabled: true,
    show: {
      preset: 'slide',
      speed: 300,
      easing: 'ease',
    },
    hide: {
      preset: 'fade',
      speed: 300,
      easing: 'ease',
      offset: 50,
    },
    shift: {
      speed: 300,
      easing: 'ease',
    },
    overlap: 150,
  },
};

@NgModule({
    imports: [NotifierModule.withConfig(notifierDefaultOptions)],
    exports: [NotifierModule]
})

export class NotificationModule {}