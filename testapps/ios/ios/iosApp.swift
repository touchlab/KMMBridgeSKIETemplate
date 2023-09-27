//
//  iosApp.swift
//  ios
//
//  Created by Kevin Galligan on 9/10/23.
//

import SwiftUI
import allshared

@main
struct iosApp: App {
    private let handle: SDKHandle
    
    init() {
        self.handle = StartSDKKt.startSDK(analytics: IosAnalytics())
        handle.appAnalytics.appStarted()
        print(StartSDKKt.sayHello())
        
    }
    
    var body: some Scene {
        WindowGroup {
            BreedListScreen(
                viewModel: BreedViewModel(repository: handle.breedRepository, breedAnalytics: handle.breedAnalytics),
                breedAnalytics: handle.breedAnalytics
            )
        }
    }
}

class IosAnalytics: Analytics {
    func sendEvent(eventName: String, eventArgs: [String : Any]) {
        // In a real app, you would call to your analytics backend here
        print("\(eventName) - \(eventArgs)")
    }
}
