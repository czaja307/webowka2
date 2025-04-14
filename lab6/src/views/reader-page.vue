<script setup lang="ts">
import {computed, ref} from 'vue'
import {useMutation, useQuery, useQueryClient} from "@tanstack/vue-query";
import {Button} from "@/components/ui/button";
import {ArrowLeftIcon, ArrowRightIcon} from "@radix-icons/vue";
import {Input} from "@/components/ui/input";
import {Card, CardContent} from "@/components/ui/card";
import {Checkbox} from "@/components/ui/checkbox";
import {Dialog, DialogClose, DialogContent, DialogHeader, DialogTitle, DialogTrigger,} from '@/components/ui/dialog'


interface Reader {
  id: string
  firstName: string
  lastName: string
}


interface ReaderResponse {
  content: Author[]
  totalPages: number
}


const queryClient = useQueryClient();

const page = ref(0);
const totalPages = ref(1);
const itemsPerPage = ref(3);
const editingId = ref(null);
const currentReader = ref<Reader>({
  id: '',
  firstName: 'string',
  lastName: 'string',
});

const queryKey = computed(() => {
  return ['readers', page.value, itemsPerPage.value];
});

const fetchReaders = async ({queryKey}: {
  queryKey: (string | number)[]
}) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/readers?page=${page.value}&size=${itemsPerPage.value}`);
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  const data = await response.json() as ReaderResponse;
  totalPages.value = data.totalPages;
  console.log(totalPages.value)
  return data;
};

const {data, error, isLoading, isError} = useQuery({
  queryKey: queryKey.value,
  queryFn: fetchReaders,
  keepPreviousData: true,
})


const readers = computed(() => data.value ? data.value.content : [])

const deleteMutation = useMutation({
  mutationFn: async (id: string) => {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/readers/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return id;
  },
  onSuccess: (id) => {
    queryClient.invalidateQueries({queryKey: ['readers']});
  },
});

const deleteBook = async (id: string) => {
  await deleteMutation.mutateAsync(id);
};

const updateMutation = useMutation({
  mutationFn: async ({method, id, updatedData}: {
    method: "PUT" | "POST",
    id: string | null,
    updatedData: Partial<Book>
  }) => {
    let endpoint = `${import.meta.env.VITE_API_URL}/readers/${id}`;
    if (method === "POST") {
      endpoint = `${import.meta.env.VITE_API_URL}/readers`;
    }
    const response = await fetch(endpoint, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(updatedData),
    });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json() as Promise<Reader>;
  },
  onSuccess: () => {
    queryClient.invalidateQueries({queryKey: ['readers']});
  },
});

const nextPage = () => {
  if (page.value < totalPages.value - 1) {
    page.value++;
  }
  queryClient.invalidateQueries({queryKey: ['readers']});
};

const prevPage = () => {
  if (page.value > 0) {
    page.value--;
  }
  queryClient.invalidateQueries({queryKey: ['readers']});
};

const startEditing = (reader: Reader) => {
  editingId.value = reader.id;
  currentReader.value = {
    id: reader.id,
    firstName: reader.firstName,
    lastName: reader.lastName,
  };
};

const saveReader = async (isBeingAdded?: boolean) => {
  await updateMutation.mutateAsync(
      {
        method: isBeingAdded ? "POST" : "PUT",
        id: editingId.value,
        updatedData: {
          firstName: currentReader.value.firstName,
          lastName: currentReader.value.lastName,
        }
      });
  editingId.value = null;
  currentReader.value = {
    id: '',
    firstName: 'string',
    lastName: 'string',
  };
};


const cancelEditing = () => {
  editingId.value = null;
  currentReader.value = {
    id: '',
    firstName: 'string',
    lastName: 'string',
  };
};

</script>


<template>
  <div class="flex items-center h-screen flex-col gap-10 p-4">
    <div class="flex flex-row w-full justify-between">
      <h1 class="font-bold text-xl">Readers</h1>
      <Dialog>
        <DialogTrigger as-child>
          <Button>Add</Button>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add a new reader</DialogTitle>
          </DialogHeader>
          <Card class="sm:p-4 p-1">
            <CardContent class="flex flex-col gap-4">
              <div class="justify-start flex flex-col">
                <label for="name" class="text-sm text-gray-600">First Name</label>
                <Input
                    v-model="currentReader.firstName"
                    type="text"
                    id="name"
                />
              </div>
              <div class="justify-start flex flex-col">
                <label for="lname" class="text-sm text-gray-600">Last Name</label>
                <Input
                    v-model="currentReader.lastName"
                    type="text"
                    id="lname"
                />
              </div>
              <div class="justify-start flex flex-col">
                <Button
                    class="mt-2"
                    @click="saveReader(true)"
                >
                  Save
                </Button>
                <DialogClose as-child>
                  <Button
                      variant="outline"
                      class="mt-2"
                      @click="cancelEditing"
                  >
                    Cancel
                  </Button>
                </DialogClose>
              </div>
            </CardContent>
          </Card>
        </DialogContent>
      </Dialog>
    </div>

    <div v-if="isLoading">
      Loading...
    </div>

    <div v-else-if="isError">
      Error: {{ error.message }}
    </div>

    <div v-else>
      <ul>
        <li v-for="reader in readers" :key="reader.id">
          <div class="p-10">
            <Card class="sm:p-4 p-1">
              <CardContent class="flex flex-col gap-4">
                <div class="justify-start flex flex-col">
                  <label for="Name" class="text-sm text-gray-600">Name</label>
                  <Input
                      v-if="editingId !== reader.id"
                      v-model="reader.firstName"
                      type="text"
                      id="Name"
                      :disabled="editingId !== reader.id"
                  />
                  <Input
                      v-if="editingId === reader.id"
                      v-model="currentReader.firstName"
                      type="text"
                      id="Name"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <label for="lName" class="text-sm text-gray-600">Last Name</label>
                  <Input
                      v-if="editingId !== reader.id"
                      v-model="reader.lastName"
                      type="text"
                      id="lName"
                      :disabled="editingId !== reader.id"
                  />
                  <Input
                      v-if="editingId === reader.id"
                      v-model="currentReader.lastName"
                      type="text"
                      id="lName"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <Button
                      v-if="editingId !== reader.id"
                      class="mt-2"
                      @click="startEditing(reader)"
                  >
                    Edit
                  </Button>
                  <Button
                      v-if="editingId !== reader.id"
                      class="mt-2"
                      @click="deleteBook(reader.id)"
                      variant="destructive"
                  >Delete
                  </Button>
                  <Button
                      v-if="editingId === reader.id"
                      class="mt-2"
                      @click="saveReader(false)"
                  >
                    Save
                  </Button>
                  <Button
                      v-if="editingId === reader.id"
                      variant="outline"
                      class="mt-2"
                      @click="cancelEditing"
                  >
                    Cancel
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </li>
      </ul>

    </div>

    <div class="flex flex-row gap-4 items-center p-4">
      <Button variant="outline" @click="prevPage">
        <ArrowLeftIcon/>
      </Button>
      <p>
        {{ page + 1 }}
      </p>
      <Button variant="outline" @click="nextPage">
        <ArrowRightIcon/>
      </Button>
    </div>


  </div>

</template>

<style scoped>

</style>