const routes = [
  {
    path: '/',
    redirect: to => {
      // the function receives the target route as the argument and we return a redirect path/location here.
      return {path: '/projects', name: 'projects'}
    },
  },
  {
    path: '/project/:id',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {path: 'home', name: 'home', component: () => import('pages/datasources/HomeDashboard.vue')},
      {path: 'schema', name: 'schema', component: () => import('pages/datasources/Schema.vue')},
      {path: 'repositories', name: 'repositories', component: () => import('pages/datasources/Repositories.vue')},
      {path: 'datasources', name: 'datasources', component: () => import('pages/datasources/DataSources.vue')},
      {path: 'configureIntegration', name: 'dsIntegration', component: () => import('pages/datasources/DataSourceIntegration.vue')},
      {path: 'query', name: 'query', component: () => import('pages/datasources/Query.vue')},
      {path: 'intents-list', name: 'intents-list', component: () => import('pages/datasources/IntentsList.vue')},
      {path: 'intents', name: 'intents', component: () => import('pages/datasources/CreateIntent.vue')},
      {path: 'abstract-planner', name: 'abstract-planner', component: () => import('pages/datasources/CreateIntent.vue')},
      {path: 'logical-planner', name: 'logical-planner', component: () => import('pages/datasources/CreateIntent.vue')},
      {path: 'workflow-planner', name: 'workflow-planner', component: () => import('pages/datasources/CreateIntent.vue')},
      {path: 'intent-workflows', name: 'intent-workflows', component: () => import('pages/datasources/CreateIntent.vue')},
      {path: 'data-products', name: 'data-products', component: () => import('pages/DataProducts.vue')},
    ]
  },
  {
    path: '/projects',
    component: () => import('layouts/ProjectLayout.vue'),
    children: [
      {path: '', name: 'projects', component: () => import('pages/Projects.vue')}
    ]
  },

  // When the specified route is not found, redirect to the 404 page
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
]

export default routes
