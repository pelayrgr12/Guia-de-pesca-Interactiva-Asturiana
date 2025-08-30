import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.guiapescaasturiana.app',
  appName: 'Guía de Pesca Asturiana',
  webDir: 'www',
  server: {
    androidScheme: 'https',
    cleartext: true
  }
};

export default config;
