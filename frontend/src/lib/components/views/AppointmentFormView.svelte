<script lang="ts">
  import { onMount } from 'svelte';
  import {
    appointmentsApi,
    clientsApi,
    specialistsApi,
    type AppointmentStatus,
    type Client,
    type Specialist
  } from '$lib/api';
  import DatetimeInput from '$lib/components/DatetimeInput.svelte';
  import StringInput from '$lib/components/StringInput.svelte';

  type ClientMode = 'existing' | 'new';

  let error = $state('');
  let loading = $state(true);
  let saving = $state(false);

  let description = $state('');
  let startTime = $state('');
  let endTime = $state('');
  let status = $state<AppointmentStatus>('reserved');

  let specialists = $state<Specialist[]>([]);
  let clients = $state<Client[]>([]);

  let specialistId = $state('');
  let clientMode = $state<ClientMode>('existing');
  let clientId = $state('');
  let newClientFirstName = $state('');
  let newClientLastName = $state('');

  async function loadData() {
    loading = true;
    error = '';
    try {
      const [specialistList, clientList] = await Promise.all([
        specialistsApi.list(),
        clientsApi.list()
      ]);

      specialists = specialistList;
      clients = clientList;
    } catch (e) {
      error = e instanceof Error ? e.message : 'Tietojen lataus epäonnistui';
    } finally {
      loading = false;
    }
  }

  async function createAppointment() {
    error = '';
    saving = true;

    try {
      let resolvedClientId: number | null = null;

      if (clientMode === 'existing') {
        resolvedClientId = clientId ? Number(clientId) : null;
      } else {
        if (!newClientFirstName.trim() || !newClientLastName.trim()) {
          throw new Error('Anna uuden asiakkaan etu- ja sukunimi');
        }

        const createdClient = await clientsApi.create({
          firstName: newClientFirstName.trim(),
          lastName: newClientLastName.trim()
        });

        resolvedClientId = createdClient.id;
        clients = [...clients, createdClient];
        clientId = String(createdClient.id);
        clientMode = 'existing';
        newClientFirstName = '';
        newClientLastName = '';
      }

      const resolvedSpecialistId = specialistId ? Number(specialistId) : null;

      if (!resolvedSpecialistId) {
        throw new Error('Valitse asiantuntija');
      }

      await appointmentsApi.create({
        description,
        startTime,
        endTime,
        status,
        clientId: resolvedClientId,
        specialistId: resolvedSpecialistId
      });

      description = '';
      startTime = '';
      endTime = '';
      status = 'reserved';
      specialistId = '';
      await loadData();
    } catch (e) {
      error = e instanceof Error ? e.message : 'Luonti epäonnistui';
    } finally {
      saving = false;
    }
  }

  onMount(loadData);
</script>

<section class="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
    <h2 class="mb-4 text-lg font-semibold text-slate-900">Varaus: luonti / muokkaus</h2>

    {#if error}
        <div class="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
        </div>
    {/if}

    {#if loading}
        <p class="text-slate-600">Ladataan asiantuntijat ja asiakkaat...</p>
    {:else}
        <form on:submit|preventDefault={createAppointment} class="grid gap-4">
            <StringInput
                    bind:value={description}
                    title="Kuvaus"
                    placeholder="Esim. Hiustenleikkaus"
                    id="description"
            />

            <div class="grid gap-4 md:grid-cols-2">
                <DatetimeInput bind:value={startTime} title="Alkuaika" />
                <DatetimeInput bind:value={endTime} title="Loppuaika" />
            </div>

            <div>
                <label class="mb-1 block text-sm font-medium text-slate-700" for="specialistId">
                    Asiantuntija
                </label>
                <select
                        id="specialistId"
                        bind:value={specialistId}
                        required
                        class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-slate-900 outline-none ring-indigo-200 transition focus:border-indigo-500 focus:ring"
                >
                    <option value="">Valitse asiantuntija</option>
                    {#each specialists as specialist}
                        <option value={specialist.id}>
                            {specialist.firstName} {specialist.lastName}
                        </option>
                    {/each}
                </select>
            </div>

            <div class="rounded-xl border border-slate-200 bg-slate-50 p-4">
                <h3 class="mb-3 text-sm font-semibold text-slate-900">Asiakas</h3>

                <div class="mb-4 flex gap-2">
                    <button
                            type="button"
                            class={`rounded-lg px-3 py-2 text-sm font-medium ${
							clientMode === 'existing'
								? 'bg-indigo-600 text-white'
								: 'bg-white text-slate-700 border border-slate-300'
						}`}
                            on:click={() => (clientMode = 'existing')}
                    >
                        Valitse asiakas
                    </button>

                    <button
                            type="button"
                            class={`rounded-lg px-3 py-2 text-sm font-medium ${
							clientMode === 'new'
								? 'bg-indigo-600 text-white'
								: 'bg-white text-slate-700 border border-slate-300'
						}`}
                            on:click={() => (clientMode = 'new')}
                    >
                        Luo uusi asiakas
                    </button>
                </div>

                {#if clientMode === 'existing'}
                    <div>
                        <label class="mb-1 block text-sm font-medium text-slate-700" for="clientId">
                            Asiakas
                        </label>
                        <select
                                id="clientId"
                                bind:value={clientId}
                                class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-slate-900 outline-none ring-indigo-200 transition focus:border-indigo-500 focus:ring"
                        >
                            <option value="">Ei valittu</option>
                            {#each clients as client}
                                <option value={client.id}>
                                    {client.firstName} {client.lastName}
                                </option>
                            {/each}
                        </select>
                    </div>
                {:else}
                    <div class="grid gap-4 md:grid-cols-2">
                        <StringInput
                                bind:value={newClientFirstName}
                                title="Asiakkaan etunimi"
                                placeholder="Matti"
                                id="newClientFirstName"
                        />
                        <StringInput
                                bind:value={newClientLastName}
                                title="Asiakkaan sukunimi"
                                placeholder="Meikäläinen"
                                id="newClientLastName"
                        />
                    </div>
                {/if}
            </div>
            
            <button
                    type="submit"
                    disabled={saving}
                    class="inline-flex items-center justify-center rounded-lg bg-indigo-600 px-4 py-2 font-medium text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:opacity-60 focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:ring-offset-2"
            >
                {saving ? 'Tallennetaan...' : 'Luo ajanvaraus'}
            </button>
        </form>
    {/if}
</section>