<script lang="ts">
  import ViewMenu from '$lib/components/ViewMenu.svelte';
  import AppointmentFormView from '$lib/components/views/AppointmentFormView.svelte';
  import SpecialistFormView from '$lib/components/views/SpecialistFormView.svelte';
  import AppointmentSearchView from '$lib/components/views/AppointmentSearchView.svelte';
  import SpecialistSearchView from '$lib/components/views/SpecialistSearchView.svelte';
  import type { ViewMenuItem, ViewMode } from '$lib/types/view';

  let activeView = $state<ViewMode>('appointment-form');

  const menuItems: ViewMenuItem[] = [
    {
      id: 'appointment-form',
      label: 'Varaus: luonti / muokkaus',
      description: 'Luo uusi varaus tai lisää myöhemmin muokkaus.'
    },
    {
      id: 'specialist-form',
      label: 'Asiantuntija: luonti / muokkaus',
      description: 'Luo uusi asiantuntija tai lisää myöhemmin muokkaus.'
    },
    {
      id: 'appointment-search',
      label: 'Varausten haku',
      description: 'Hae ja selaa varauksia.'
    },
    {
      id: 'specialist-search',
      label: 'Asiantuntijoiden haku',
      description: 'Hae ja selaa asiantuntijoita.'
    }
  ];

  function changeView(next: ViewMode) {
    activeView = next;
  }
</script>

<main class="min-h-screen bg-slate-50 py-10">
    <div class="mx-auto w-full max-w-5xl px-4">
        <h1 class="mb-6 text-3xl font-bold tracking-tight text-slate-900">Ajanvaraukset</h1>

        <ViewMenu items={menuItems} active={activeView} onChange={changeView} />

        {#if activeView === 'appointment-form'}
            <AppointmentFormView />
        {:else if activeView === 'specialist-form'}
            <SpecialistFormView />
        {:else if activeView === 'appointment-search'}
            <AppointmentSearchView />
        {:else if activeView === 'specialist-search'}
            <SpecialistSearchView />
        {/if}
    </div>
</main>